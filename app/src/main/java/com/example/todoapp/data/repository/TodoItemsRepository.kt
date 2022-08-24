package com.example.todoapp.data.repository

import android.util.Log
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.PrepareRequests
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.UpdateItemResponse
import com.example.todoapp.di.scopes.AppScope
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScope
class TodoItemsRepository @Inject constructor(
    private val databaseDao: TodoItemsDao,
    private val todoApi: TodoApi,
    private val sessionManager: SessionManager,
    private val prepareRequests: PrepareRequests,
    private val checkInternet: CheckInternet
) {
    fun getTodoItemsLivaData() = databaseDao.getAllTodoItemsLive()

    private suspend fun getTodoItems() = withContext(Dispatchers.IO) {
        databaseDao.getAllTodoItems().sortedBy { todoItem ->
            todoItem.createdAt
        }
    }

    private suspend fun retrofitCall(call: suspend () -> (UpdateItemResponse)) {
        retrofitCallInFlow { call() }.retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            Log.e("network", "Request failure: ${it.message}")
        }.collect()
    }

    private fun retrofitCallInFlow(call: suspend () -> (UpdateItemResponse)): Flow<UpdateItemResponse> {
        return flow {
            val updateItemResponse = checkInternet.callWithInternetCheck { call() }
            sessionManager.saveRevisionNetwork(updateItemResponse.revision)
            emit(updateItemResponse)
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun synchronizationData(todoItems: List<TodoItem>) {
        val revisionNetwork = sessionManager.fetchRevisionNetwork()
        val revisionDatabase = sessionManager.fetchRevisionDatabase()

        if (revisionNetwork > revisionDatabase) {
            synchronizationDatabase(todoItems)
        } else {
            synchronizationNetwork()
        }
    }

    private suspend fun synchronizationDatabase(todoItems: List<TodoItem>) {
        if (getTodoItems().isNotEmpty()) {
            databaseDao.deleteAllTodoItems()
        }
        val newList = mergeData(getTodoItems(), todoItems)
        databaseDao.addAllTodoItems(newList)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionNetwork())
    }

    // Слияние данных происходит по принципу - в приоритете данные из сети, но если элемент
    // из локальной базы данных изменялся позже, чем элемент с таким же id из сети, тогда добавляем его
    private fun mergeData(
        listFromDatabase: List<TodoItem>,
        listFromNetwork: List<TodoItem>
    ): List<TodoItem> {
        val newList = mutableListOf<TodoItem>()
        listFromNetwork.forEach { todoItemFromNetwork ->
            val todoItem = listFromDatabase.find { todoItemFromDatabase ->
                todoItemFromNetwork.id == todoItemFromDatabase.id
            }
            if (todoItem == null) {
                newList.add(todoItemFromNetwork)
            } else if (todoItem.changedAt ?: 0 > todoItemFromNetwork.changedAt ?: 0) {
                newList.add(todoItem)
            } else newList.add(todoItemFromNetwork)
        }
        return newList
    }

    private suspend fun synchronizationNetwork() {
        val newRevisionNetwork = sessionManager.fetchRevisionDatabase() - 1
        patchTodoItemsNetwork(getTodoItems(), newRevisionNetwork.toString())
    }

    suspend fun getTodoItemsNetwork() = withContext(Dispatchers.IO) {
        getTodoItemsInFlow().catch {
            Log.e("network", "Request failure ${it.message}")
        }.collect { todoItems ->
            synchronizationData(todoItems)
        }
    }

    private suspend fun getTodoItemsInFlow(): Flow<List<TodoItem>> {
        return flow {
            val getItemsResponse = checkInternet.callWithInternetCheck { todoApi.getTodoItems() }
            sessionManager.saveRevisionNetwork(getItemsResponse.revision)
            emit(getListAfterGetRequest(getItemsResponse))
        }.flowOn(Dispatchers.IO)
    }

    private fun getListAfterGetRequest(body: GetItemsResponse) =
        body.todoItemsNetwork.map { it.mapToTodoItem() }

    suspend fun addTodoItem(todoItem: TodoItem, id: String?) = withContext(Dispatchers.IO) {
        val newTodoItem = prepareRequests.prepareAddTodoItemRequest(todoItem, id)
        addTodoItemDatabase(newTodoItem)
        postTodoItemNetwork(newTodoItem)
    }

    private suspend fun addTodoItemDatabase(todoItem: TodoItem) {
        databaseDao.addTodoItem(todoItem)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionDatabase() + 1)
    }

    private suspend fun postTodoItemNetwork(todoItem: TodoItem) {
        val updateItemRequest = prepareRequests.preparePostRequest(todoItem)
        retrofitCall { todoApi.postTodoItem(updateItemRequest) }
    }

    suspend fun editTodoItem(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        editTodoItemDatabase(todoItem)
        putTodoItemNetwork(todoItem)
    }

    private suspend fun editTodoItemDatabase(todoItem: TodoItem) {
        databaseDao.editTodoItem(todoItem)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionDatabase() + 1)
    }

    private suspend fun putTodoItemNetwork(todoItem: TodoItem) {
        val updateItemRequest = prepareRequests.preparePutRequest(todoItem)
        retrofitCall { todoApi.putTodoItem(todoItem.id.toString(), updateItemRequest) }
    }

    suspend fun deleteTodoItem(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        deleteTodoItemDatabase(todoItem)
        deleteTodoItemNetwork(todoItem.id.toString())
    }

    private suspend fun deleteTodoItemDatabase(todoItem: TodoItem) {
        databaseDao.deleteTodoItem(todoItem)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionDatabase() + 1)
    }

    private suspend fun deleteTodoItemNetwork(id: String) {
        retrofitCall { todoApi.deleteTodoItem(id) }
    }

    private suspend fun patchTodoItemsNetwork(todoItems: List<TodoItem>, newRevision: String) {
        val setItemsRequest = prepareRequests.preparePatchRequest(todoItems)

        patchTodoItemsInFlow(setItemsRequest, newRevision).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            Log.e("network", "Request failure ${it.message}")
        }.collect()
    }

    private fun patchTodoItemsInFlow(
        setItemsRequest: SetItemsRequest,
        newRevision: String
    ): Flow<GetItemsResponse> {
        return flow {
            val patchItemsResponse =
                checkInternet.callWithInternetCheck {
                    todoApi.patchTodoItem(newRevision, setItemsRequest)
                }
            sessionManager.saveRevisionNetwork(patchItemsResponse.revision)
            emit(patchItemsResponse)
        }.flowOn(Dispatchers.IO)
    }

    companion object {
        const val NETWORK_RETRY_DELAY = 1000L
    }
}