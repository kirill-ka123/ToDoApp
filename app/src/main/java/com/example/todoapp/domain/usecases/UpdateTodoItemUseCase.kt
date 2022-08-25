package com.example.todoapp.domain.usecases

import android.util.Log
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.PrepareRequests
import com.example.todoapp.data.network.models.UpdateItemResponse
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.di.scopes.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

@AppScope
class UpdateTodoItemUseCase @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    private val sessionManager: SessionManager,
    private val prepareRequests: PrepareRequests,
    private val checkInternet: CheckInternet
) {
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
            val updateItemResponse = callWithInternetCheck { call() }
            sessionManager.saveRevisionNetwork(updateItemResponse.revision)
            emit(updateItemResponse)
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun <T> callWithInternetCheck(
        call: suspend () -> (T)
    ): T {
        if (checkInternet.hasInternetConnection()) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    suspend fun addTodoItem(todoItem: TodoItem, id: String?) {
        val newTodoItem = prepareRequests.prepareAddTodoItemRequest(todoItem, id)
        addTodoItemDatabase(newTodoItem)
        addTodoItemNetwork(newTodoItem)
    }

    private suspend fun addTodoItemDatabase(todoItem: TodoItem) {
        todoItemsRepository.addTodoItemDatabase(todoItem)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionDatabase() + 1)
    }

    private suspend fun addTodoItemNetwork(todoItem: TodoItem) {
        val updateItemRequest = prepareRequests.preparePostRequest(todoItem)
        retrofitCall { todoItemsRepository.postTodoItemNetwork(updateItemRequest) }
    }

    suspend fun editTodoItem(todoItem: TodoItem) {
        editTodoItemDatabase(todoItem)
        putTodoItemNetwork(todoItem)
    }

    private suspend fun editTodoItemDatabase(todoItem: TodoItem) {
        todoItemsRepository.editTodoItemDatabase(todoItem)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionDatabase() + 1)
    }

    private suspend fun putTodoItemNetwork(todoItem: TodoItem) {
        val updateItemRequest = prepareRequests.preparePutRequest(todoItem)
        retrofitCall { todoItemsRepository.putTodoItemNetwork(todoItem.id, updateItemRequest) }
    }

    suspend fun deleteTodoItem(todoItem: TodoItem) {
        deleteTodoItemDatabase(todoItem)
        deleteTodoItemNetwork(todoItem.id)
    }

    private suspend fun deleteTodoItemDatabase(todoItem: TodoItem) {
        todoItemsRepository.deleteTodoItemDatabase(todoItem)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionDatabase() + 1)
    }

    private suspend fun deleteTodoItemNetwork(id: String) {
        retrofitCall { todoItemsRepository.deleteTodoItemNetwork(id) }
    }

    companion object {
        const val NETWORK_RETRY_DELAY = 1000L
    }
}