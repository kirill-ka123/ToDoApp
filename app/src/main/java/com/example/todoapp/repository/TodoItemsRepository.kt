package com.example.todoapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.R
import com.example.todoapp.common.Event
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem
import com.example.todoapp.network.RetrofitInstance
import com.example.todoapp.network.models.GetItemsResponse
import com.example.todoapp.network.models.SetItemRequest
import com.example.todoapp.network.models.SetItemResponse
import com.example.todoapp.network.models.TodoItemNetwork
import retrofit2.Response
import java.io.IOException

class TodoItemsRepository(private val sourceData: MutableList<TodoItem>) {
    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null
        private val lock = Any()

        fun getRepository() = instance ?: synchronized(lock) {
            instance ?: TodoItemsRepository(SourceData.todoItems).also { instance = it }
        }
    }

    private val _message = MutableLiveData<Event>()
    val message: LiveData<Event> = _message

    private val _todoItemsLiveData: MutableLiveData<List<TodoItem>> = MutableLiveData()
    val todoItemsLiveData: LiveData<List<TodoItem>> = _todoItemsLiveData

//    private val todoItemsMutableState: MutableStateFlow<List<TodoItem>> = MutableStateFlow(listOf())
//    val todoItemsState: StateFlow<List<TodoItem>>
//        get() = todoItemsMutableState

    fun setTodoItemsLiveData(todoItems: List<TodoItem>) {
        _todoItemsLiveData.postValue(todoItems)
    }

    fun refreshLiveData() {
        todoItemsLiveData.value?.let { todoItems ->
            setTodoItemsLiveData(todoItems.toList())
        }
    }

    private suspend fun safeCall(context: Context, event: Event, call: suspend () -> Unit) {
        try {
            if (hasInternetConnection(context)) {
                call()
            } else {
                event.message = R.string.no_internet_connection
                _message.postValue(event)
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    event.message = R.string.network_failure
                    _message.postValue(event)
                }
                else -> {
                    event.message = R.string.conversion_error
                    _message.postValue(event)
                }
            }
        }
    }

    suspend fun getTodoItemsNetwork(context: Context) {
        safeCall(context, Event.GetEvent(0)) {
            val response = RetrofitInstance.getApi(context).getTodoItems()
            handleGetItemsResponse(context, response)
        }
    }

    private fun handleGetItemsResponse(context: Context, response: Response<GetItemsResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val revision = resultResponse.revision
                SessionManager(context).saveRevision(revision)

                val todoItems = resultResponse.todoItemsNetwork.map {
                    it.mapToTodoItem()
                }.sortedBy { todoItem ->
                    todoItem.id.toInt()
                }
                setTodoItemsLiveData(todoItems)
            }
        } else _message.postValue(Event.GetEvent(R.string.internal_error))
    }

    suspend fun getTodoItemByIdNetwork(context: Context, id: String) =
        RetrofitInstance.getApi(context).getTodoItemById(id)

    suspend fun postTodoItemNetwork(context: Context, todoItem: TodoItem, id: String?) {
        safeCall(context, Event.SetEvent(0)) {
            val todoItemNetwork: TodoItemNetwork = if (id != null) {
                todoItem.mapToTodoItemNetwork(id)
            } else todoItem.mapToTodoItemNetwork(generateId().toString())

            val response = RetrofitInstance.getApi(context)
                .postTodoItem(SetItemRequest(todoItemNetwork = todoItemNetwork))
            handlePostItemResponse(context, response)
        }
    }

    private fun generateId(): Int {
        todoItemsLiveData.value?.let { todoItems ->
            if (todoItems.isNotEmpty()) {
                return todoItems.size
            } else return 0
        }
        return 0
    }

    private fun handlePostItemResponse(context: Context, response: Response<SetItemResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val newItem = resultResponse.todoItemNetwork.mapToTodoItem()
                todoItemsLiveData.value?.let { todoItems ->
                    val revision = resultResponse.revision
                    SessionManager(context).saveRevision(revision)

                    val newList = todoItems.toMutableList()
                    var flag = true
                    todoItems.forEachIndexed { index, todoItem ->
                        if (newItem.id.toInt() < todoItem.id.toInt()) {
                            newList.add(index, newItem)
                            setTodoItemsLiveData(newList.toList())
                            flag = false
                            return
                        }
                    }
                    if (flag) {
                        newList.add(newItem)
                        setTodoItemsLiveData(newList.toList())
                    }
                }
            }
        } else _message.postValue(Event.SetEvent(R.string.internal_error))
    }


    suspend fun putTodoItemNetwork(context: Context, todoItem: TodoItem) {
        safeCall(context, Event.SetEvent(0)) {
            val todoItemNetwork = todoItem.mapToTodoItemNetwork(todoItem.id)
            val response = RetrofitInstance.getApi(context)
                .putTodoItem(todoItemNetwork.id, SetItemRequest(todoItemNetwork = todoItemNetwork))
            handlePutItemResponse(context, response)
        }
    }

    private fun handlePutItemResponse(context: Context, response: Response<SetItemResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val newItem = resultResponse.todoItemNetwork.mapToTodoItem()
                todoItemsLiveData.value?.let { todoItems ->
                    val revision = resultResponse.revision
                    SessionManager(context).saveRevision(revision)

                    val newList = todoItems.toMutableList()
                    todoItems.forEachIndexed { index, todoItem ->
                        if (todoItem.id.toInt() == newItem.id.toInt()) {
                            newList[index] = newItem
                            return@forEachIndexed
                        }
                    }
                    setTodoItemsLiveData(newList.toList())
                }
            }
        } else _message.postValue(Event.SetEvent(R.string.internal_error))
    }

    suspend fun deleteTodoItemNetwork(context: Context, id: String) {
        safeCall(context, Event.SetEvent(0)) {
            val response = RetrofitInstance.getApi(context).deleteTodoItem(id)
            handleDeleteItemResponse(context, response)
        }
    }

    private fun handleDeleteItemResponse(context: Context, response: Response<SetItemResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val newItem = resultResponse.todoItemNetwork.mapToTodoItem()
                todoItemsLiveData.value?.let { todoItems ->
                    val revision = resultResponse.revision
                    SessionManager(context).saveRevision(revision)

                    val newList = todoItems.toMutableList()
                    todoItems.forEachIndexed { index, todoItem ->
                        if (todoItem.id.toInt() == newItem.id.toInt()) {
                            newList.removeAt(index)
                            return@forEachIndexed
                        }
                    }
                    setTodoItemsLiveData(newList)
                }
            }
        } else _message.postValue(Event.SetEvent(R.string.internal_error))
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