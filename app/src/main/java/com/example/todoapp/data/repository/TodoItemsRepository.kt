package com.example.todoapp.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.HandleResponses
import com.example.todoapp.data.network.PrepareRequests
import com.example.todoapp.data.network.RetrofitInstance
import com.example.todoapp.data.network.models.SetItemRequest
import com.example.todoapp.data.network.models.StateRequest
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class TodoItemsRepository(
    private val appContext: Context,
    private val prepareRequests: PrepareRequests,
    private val handleResponses: HandleResponses
) {

    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null

        fun getRepository(
            appContext: Context,
            prepareRequests: PrepareRequests,
            handleResponses: HandleResponses
        ) =
            instance ?: synchronized(TodoItemsRepository::class) {
                instance ?: TodoItemsRepository(appContext, prepareRequests, handleResponses).also {
                    instance = it
                }
            }

        const val NETWORK_RETRY_DELAY = 1000L
    }

    private suspend fun <T> callWithInternetCheck(
        call: suspend () -> (Response<T>)
    ): Response<T> {
        if (CheckInternet.hasInternetConnection(appContext)) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    private val _todoItemsLiveData: MutableLiveData<List<TodoItem>> = MutableLiveData()
    val todoItemsLiveData: LiveData<List<TodoItem>> = _todoItemsLiveData

    private val _stateGetRequestLiveData: MutableLiveData<StateRequest> = MutableLiveData()
    val stateGetRequestLiveData: LiveData<StateRequest> = _stateGetRequestLiveData

    private val _stateSetRequestLiveData: MutableLiveData<StateRequest> = MutableLiveData()
    val stateSetRequestLiveData: LiveData<StateRequest> = _stateSetRequestLiveData

    suspend fun getTodoItemsNetwork() = withContext(Dispatchers.IO) {
        getTodoItemsInFlow().retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _stateGetRequestLiveData.postValue(StateRequest.Error(it))
        }.collect {
            _todoItemsLiveData.postValue(it)
            _stateGetRequestLiveData.postValue(StateRequest.Success())
        }
    }


    private suspend fun getTodoItemsInFlow(): Flow<List<TodoItem>> {
        return flow {
            val response =
                callWithInternetCheck {
                    RetrofitInstance.getApi(appContext).getTodoItems()
                }
            emit(handleResponses.handleGetResponse(response))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postTodoItemNetwork(todoItem: TodoItem, id: String?) = withContext(Dispatchers.IO) {
        val setItemRequest =
            prepareRequests.preparePostRequest(todoItemsLiveData.value.orEmpty(), todoItem, id)
        postTodoItemInFlow(setItemRequest).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _stateSetRequestLiveData.postValue(StateRequest.Error(it))
        }.collect {
            _todoItemsLiveData.postValue(it)
            _stateGetRequestLiveData.postValue(StateRequest.Success())
        }
    }

    private suspend fun postTodoItemInFlow(
        setItemRequest: SetItemRequest
    ): Flow<List<TodoItem>> {
        return flow {
            val response = callWithInternetCheck {
                RetrofitInstance.getApi(appContext).postTodoItem(setItemRequest)
            }
            emit(handleResponses.handlePostResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun putTodoItemNetwork(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        val setItemRequest = prepareRequests.preparePutRequest(todoItem)
        putTodoItemInFlow(todoItem.id, setItemRequest).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _stateSetRequestLiveData.postValue(StateRequest.Error(it))
        }.collect {
            _todoItemsLiveData.postValue(it)
            _stateGetRequestLiveData.postValue(StateRequest.Success())
        }
    }

    private fun putTodoItemInFlow(
        id: String,
        setItemRequest: SetItemRequest
    ): Flow<List<TodoItem>> {
        return flow {
            val response = callWithInternetCheck {
                RetrofitInstance.getApi(appContext).putTodoItem(id, setItemRequest)
            }
            emit(handleResponses.handlePutResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteTodoItemNetwork(id: String) = withContext(Dispatchers.IO) {
        deleteTodoItemInFlow(id).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _stateSetRequestLiveData.postValue(StateRequest.Error(it))
        }.collect {
            _todoItemsLiveData.postValue(it)
            _stateGetRequestLiveData.postValue(StateRequest.Success())
        }
    }

    private fun deleteTodoItemInFlow(id: String): Flow<List<TodoItem>> {
        return flow {
            val response = callWithInternetCheck {
                RetrofitInstance.getApi(appContext).deleteTodoItem(id)
            }
            emit(handleResponses.handleDeleteResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }
}