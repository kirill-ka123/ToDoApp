package com.example.todoapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.network.HandleResponses
import com.example.todoapp.data.network.PrepareRequests
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.SetItemRequest
import com.example.todoapp.data.network.models.StateRequest
import com.example.todoapp.di.scopes.AppScope
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@AppScope
class TodoItemsRepository @Inject constructor(
    private val appContext: Context,
    private val todoApi: TodoApi,
    private val prepareRequests: PrepareRequests,
    private val handleResponses: HandleResponses
) {
    private val _todoItemsLiveData: MutableLiveData<List<TodoItem>> = MutableLiveData()
    val todoItemsLiveData: LiveData<List<TodoItem>> = _todoItemsLiveData

    private val _stateGetRequestLiveData: MutableLiveData<StateRequest> = MutableLiveData()
    val stateGetRequestLiveData: LiveData<StateRequest> = _stateGetRequestLiveData

    private val _stateSetRequestLiveData: MutableLiveData<StateRequest> = MutableLiveData()
    val stateSetRequestLiveData: LiveData<StateRequest> = _stateSetRequestLiveData

    private suspend fun <T> callWithInternetCheck(
        call: suspend () -> (Response<T>)
    ): Response<T> {
        if (hasInternetConnection()) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

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
                    todoApi.getTodoItems()
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
                todoApi.postTodoItem(setItemRequest)
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
                todoApi.putTodoItem(id, setItemRequest)
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
                todoApi.deleteTodoItem(id)
            }
            emit(handleResponses.handleDeleteResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = appContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    companion object {
        const val NETWORK_RETRY_DELAY = 1000L
    }
}