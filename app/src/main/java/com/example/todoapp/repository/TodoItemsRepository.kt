package com.example.todoapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.common.Constants.NETWORK_RETRY_DELAY
import com.example.todoapp.common.Event
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem
import com.example.todoapp.network.RetrofitInstance
import com.example.todoapp.network.models.GetItemsResponse
import com.example.todoapp.network.models.SetItemRequest
import com.example.todoapp.network.models.SetItemResponse
import com.example.todoapp.network.models.TodoItemNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class TodoItemsRepository() {
    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null
        private val lock = Any()

        fun getRepository() = instance ?: synchronized(lock) {
            instance ?: TodoItemsRepository().also { instance = it }
        }
    }

    private val _message = MutableLiveData<Event>()
    val message: LiveData<Event> = _message

    private val _todoItemsLiveData: MutableLiveData<List<TodoItem>> = MutableLiveData()
    val todoItemsLiveData: LiveData<List<TodoItem>> = _todoItemsLiveData

    private fun setTodoItemsLiveData(todoItems: List<TodoItem>) {
        _todoItemsLiveData.postValue(todoItems)
    }

    private fun saveRevision(context: Context, response: GetItemsResponse) {
        val revision = response.revision
        SessionManager(context).saveRevision(revision)
    }

    private fun saveRevision(context: Context, response: SetItemResponse) {
        val revision = response.revision
        SessionManager(context).saveRevision(revision)
    }

    suspend fun getTodoItemsNetwork(context: Context) {
        getTodoItemsFlow(context).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _message.postValue(Event.GetEvent(it.message.toString()))
        }.collect {
            setTodoItemsLiveData(it)
        }
    }

    private suspend fun getTodoItemsFlow(context: Context): Flow<List<TodoItem>> {
        return flow {
            val response = if (hasInternetConnection(context)) {
                RetrofitInstance.getApi(context).getTodoItems()
            } else throw Exception("No Internet connection")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    saveRevision(context, body)
                    emit(body.todoItemsNetwork.map { it.mapToTodoItem() }.sortedBy { todoItem ->
                        todoItem.id.toInt()
                    })
                } else throw Exception("Body is null")
            } else throw Exception(response.message())
        }.flowOn(Dispatchers.IO)
    }

    private fun generateId(): Int {
        todoItemsLiveData.value?.let { todoItems ->
            if (todoItems.isNotEmpty()) {
                return todoItems.size
            } else return 0
        }
        return 0
    }

    suspend fun postTodoItemNetwork(context: Context, todoItem: TodoItem, id: String?) {
        postTodoItemFlow(context, todoItem, id).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _message.postValue(Event.SetEvent(it.message.toString()))
        }.collect {
            setTodoItemsLiveData(it)
        }
    }

    private suspend fun postTodoItemFlow(
        context: Context,
        todoItem: TodoItem,
        id: String?
    ): Flow<List<TodoItem>> {
        val todoItemNetwork: TodoItemNetwork = if (id != null) {
            todoItem.mapToTodoItemNetwork(id)
        } else todoItem.mapToTodoItemNetwork(generateId().toString())
        val setItemRequest = SetItemRequest(todoItemNetwork = todoItemNetwork)

        return flow {
            val response = if (hasInternetConnection(context)) {
                RetrofitInstance.getApi(context).postTodoItem(setItemRequest)
            } else throw Exception("No Internet connection")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    saveRevision(context, body)
                    emit(handlePostTodoItem(body))
                } else throw Exception("Body is null")
            } else throw Exception(response.message())
        }.flowOn(Dispatchers.IO)
    }

    private fun handlePostTodoItem(body: SetItemResponse): List<TodoItem> {
        val newItem = body.todoItemNetwork.mapToTodoItem()
        val todoItems = todoItemsLiveData.value
        if (todoItems != null) {
            val newList = todoItems.toMutableList()
            var flag = true
            todoItems.forEachIndexed { index, todoItem ->
                if (newItem.id.toInt() < todoItem.id.toInt()) {
                    newList.add(index, newItem)
                    flag = false
                    return newList.toList()
                }
            }
            if (flag) {
                newList.add(newItem)
                return newList.toList()
            }
        }
        return emptyList()
    }

    suspend fun putTodoItemNetwork(context: Context, todoItem: TodoItem) {
        putTodoItemFlow(context, todoItem).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _message.postValue(Event.SetEvent(it.message.toString()))
        }.collect {
            setTodoItemsLiveData(it)
        }
    }

    private suspend fun putTodoItemFlow(
        context: Context,
        todoItem: TodoItem
    ): Flow<List<TodoItem>> {
        val todoItemNetwork: TodoItemNetwork = todoItem.mapToTodoItemNetwork(todoItem.id)
        val setItemRequest = SetItemRequest(todoItemNetwork = todoItemNetwork)

        return flow {
            val response = if (hasInternetConnection(context)) {
                RetrofitInstance.getApi(context).putTodoItem(todoItem.id, setItemRequest)
            } else throw Exception("No Internet connection")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    saveRevision(context, body)
                    emit(handlePutTodoItem(body))
                } else throw Exception("Body is null")
            } else throw Exception(response.message())
        }.flowOn(Dispatchers.IO)
    }

    private fun handlePutTodoItem(body: SetItemResponse): List<TodoItem> {
        val newItem = body.todoItemNetwork.mapToTodoItem()
        val todoItems = todoItemsLiveData.value
        if (todoItems != null) {
            val newList = todoItems.toMutableList()
            todoItems.forEachIndexed { index, todoItem ->
                if (todoItem.id.toInt() == newItem.id.toInt()) {
                    newList[index] = newItem
                    return@forEachIndexed
                }
            }
            return newList.toList()
        }
        return emptyList()
    }

    suspend fun deleteTodoItemNetwork(context: Context, id: String) {
        deleteTodoItemFlow(context, id).retry(1) {
            delay(NETWORK_RETRY_DELAY)
            return@retry true
        }.catch {
            _message.postValue(Event.SetEvent(it.message.toString()))
        }.collect {
            setTodoItemsLiveData(it)
        }
    }

    private suspend fun deleteTodoItemFlow(context: Context, id: String): Flow<List<TodoItem>> {
        return flow {
            val response = if (hasInternetConnection(context)) {
                RetrofitInstance.getApi(context).deleteTodoItem(id)
            } else throw Exception("No Internet connection")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    saveRevision(context, body)
                    emit(handleDeleteTodoItem(body))
                } else throw Exception("Body is null")
            } else throw Exception(response.message())
        }.flowOn(Dispatchers.IO)
    }

    private fun handleDeleteTodoItem(body: SetItemResponse): List<TodoItem> {
        val deletedItem = body.todoItemNetwork.mapToTodoItem()
        val todoItems = todoItemsLiveData.value
        if (todoItems != null) {
            val newList = todoItems.toMutableList()
            todoItems.forEachIndexed { index, todoItem ->
                if (todoItem.id.toInt() == deletedItem.id.toInt()) {
                    newList.removeAt(index)
                    return@forEachIndexed
                }
            }
            return newList.toList()
        }
        return emptyList()
    }

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}