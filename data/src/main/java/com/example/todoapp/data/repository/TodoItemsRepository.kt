package com.example.todoapp.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.common.Constants.NETWORK_RETRY_DELAY
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.NetworkUtils
import com.example.todoapp.data.network.RetrofitInstance
import com.example.todoapp.data.network.handling.HandleDeleteResponse
import com.example.todoapp.data.network.handling.HandleGetResponse
import com.example.todoapp.data.network.handling.HandlePostResponse
import com.example.todoapp.data.network.handling.HandlePutResponse
import com.example.todoapp.data.network.models.SetItemRequest
import com.example.todoapp.data.network.models.StateRequest
import com.example.todoapp.data.network.preparing.PreparePostRequest
import com.example.todoapp.data.network.preparing.PreparePutRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

object TodoItemsRepository {
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
                NetworkUtils.callWithInternetCheck(context) {
                    RetrofitInstance.getApi(context).getTodoItems()
                }
            emit(HandleGetResponse.invoke(context, response))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postTodoItemNetwork(context: Context, todoItem: TodoItem, id: String?) {
        val setItemRequest =
            PreparePostRequest.invoke(todoItemsLiveData.value.orEmpty(), todoItem, id)
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
            val response = NetworkUtils.callWithInternetCheck(context) {
                RetrofitInstance.getApi(context).postTodoItem(setItemRequest)
            }
            emit(HandlePostResponse.invoke(context, response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun putTodoItemNetwork(context: Context, todoItem: TodoItem) {
        val setItemRequest = PreparePutRequest.invoke(todoItem)
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
            val response = NetworkUtils.callWithInternetCheck(context) {
                RetrofitInstance.getApi(context).putTodoItem(id, setItemRequest)
            }
            emit(HandlePutResponse.invoke(context, response, todoItemsLiveData.value.orEmpty()))
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
            val response = NetworkUtils.callWithInternetCheck(context) {
                RetrofitInstance.getApi(context).deleteTodoItem(id)
            }
            emit(HandleDeleteResponse.invoke(context, response, todoItemsLiveData.value.orEmpty()))
        }.flowOn(Dispatchers.IO)
    }
}