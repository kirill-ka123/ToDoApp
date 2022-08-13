package com.example.todoapp.presentation.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.presentation.data.network.CheckInternet
import com.example.todoapp.presentation.data.network.HandleResponses
import com.example.todoapp.presentation.data.network.PrepareRequests
import com.example.todoapp.presentation.data.network.RetrofitInstance
import com.example.todoapp.presentation.data.network.models.SetItemRequest
import com.example.todoapp.presentation.data.network.models.StateRequest
import com.example.todoapp.presentation.models.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.Response
import java.io.IOException

class TodoItemsRepository(
    private val prepareRequests: PrepareRequests,
    private val handleResponses: HandleResponses
) {

    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null
        private val lock = Any()

        fun getRepository(prepareRequests: PrepareRequests, handleResponses: HandleResponses) =
            instance ?: synchronized(lock) {
                instance ?: TodoItemsRepository(prepareRequests, handleResponses).also {
                    instance = it
                }
            }

        const val NETWORK_RETRY_DELAY = 1000L
    }

    private suspend fun <T> callWithInternetCheck(
        context: Context,
        call: suspend () -> (Response<T>)
    ): Response<T> {
        if (CheckInternet.hasInternetConnection(context)) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    private val _todoItemsLiveData: MutableLiveData<List<TodoItem>> = MutableLiveData()
    val todoItemsLiveData: LiveData<List<TodoItem>> = _todoItemsLiveData

    private val _stateGetRequestLiveData: MutableLiveData<StateRequest> = MutableLiveData()
    val stateGetRequestLiveData: LiveData<StateRequest> = _stateGetRequestLiveData

    private val _stateSetRequestLiveData: MutableLiveData<StateRequest> = MutableLiveData()
    val stateSetRequestLiveData: LiveData<StateRequest> = _stateSetRequestLiveData

    suspend fun getTodoItemsNetwork(context: Context) {
        getTodoItemsInFlow(context).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _stateGetRequestLiveData.postValue(StateRequest.Error(it))
        }.collect {
            _todoItemsLiveData.postValue(it)
            _stateGetRequestLiveData.postValue(StateRequest.Success())
        }
    }

    private suspend fun getTodoItemsInFlow(context: Context): Flow<List<TodoItem>> {
        return flow {
            val response =
                callWithInternetCheck(context) {
                    RetrofitInstance.getApi(context).getTodoItems()
                }
            emit(handleResponses.handleGetResponse(response))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postTodoItemNetwork(context: Context, todoItem: TodoItem, id: String?) {
        val setItemRequest =
            prepareRequests.preparePostRequest(todoItemsLiveData.value.orEmpty(), todoItem, id)
        postTodoItemInFlow(context, setItemRequest).retry(1) {
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
        context: Context,
        setItemRequest: SetItemRequest
    ): Flow<List<TodoItem>> {
        return flow {
            val response = callWithInternetCheck(context) {
                RetrofitInstance.getApi(context).postTodoItem(setItemRequest)
            }
            emit(handleResponses.handlePostResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun putTodoItemNetwork(context: Context, todoItem: TodoItem) {
        val setItemRequest = prepareRequests.preparePutRequest(todoItem)
        putTodoItemInFlow(context, todoItem.id, setItemRequest).retry(1) {
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
        context: Context,
        id: String,
        setItemRequest: SetItemRequest
    ): Flow<List<TodoItem>> {
        return flow {
            val response = callWithInternetCheck(context) {
                RetrofitInstance.getApi(context).putTodoItem(id, setItemRequest)
            }
            emit(handleResponses.handlePutResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteTodoItemNetwork(context: Context, id: String) {
        deleteTodoItemInFlow(context, id).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _stateSetRequestLiveData.postValue(StateRequest.Error(it))
        }.collect {
            _todoItemsLiveData.postValue(it)
            _stateGetRequestLiveData.postValue(StateRequest.Success())
        }
    }

    private fun deleteTodoItemInFlow(context: Context, id: String): Flow<List<TodoItem>> {
        return flow {
            val response = callWithInternetCheck(context) {
                RetrofitInstance.getApi(context).deleteTodoItem(id)
            }
            emit(handleResponses.handleDeleteResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }
}