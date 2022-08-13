package com.example.todoapp.presentation.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.presentation.models.TodoItem
import com.example.todoapp.presentation.data.network.HandleResponses
import com.example.todoapp.presentation.data.network.PrepareRequests
import com.example.todoapp.presentation.data.network.TodoApi
import com.example.todoapp.presentation.data.network.models.SetItemRequest
import com.example.todoapp.presentation.data.network.models.StateRequest
import com.example.todoapp.presentation.di.scopes.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@AppScope
class TodoItemsRepository @Inject constructor(
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
        context: Context,
        call: suspend () -> (Response<T>)
    ): Response<T> {
        if (hasInternetConnection(context)) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

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
                    todoApi.getTodoItems()
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
                todoApi.postTodoItem(setItemRequest)
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
                todoApi.putTodoItem(id, setItemRequest)
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
                todoApi.deleteTodoItem(id)
            }
            emit(handleResponses.handleDeleteResponse(response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
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