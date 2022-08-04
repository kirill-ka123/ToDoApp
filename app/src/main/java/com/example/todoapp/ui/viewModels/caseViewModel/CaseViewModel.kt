package com.example.todoapp.ui.viewModels.caseViewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.SessionManager
import com.example.todoapp.models.TodoItem
import com.example.todoapp.network.models.SetItemRequest
import com.example.todoapp.network.models.SetItemResponse
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class CaseViewModel(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : AndroidViewModel(app) {

//    fun saveTodoItem(todoItem: TodoItem) = viewModelScope.launch {
//        todoItemsRepository.upsertTodoItem(todoItem)
//    }
//
//    fun deleteTodoItem(todoItem: TodoItem) = viewModelScope.launch {
//        todoItemsRepository.deleteTodoItem(todoItem)
//    }

    fun postTodoItemNetwork(todoItem: TodoItem) = CoroutineScope(Dispatchers.Main).launch {
        postTodoItemSafeCall(todoItem)
    }

    private suspend fun postTodoItemSafeCall(todoItem: TodoItem) {
        try {
            if (hasInternetConnection()) {
                val id = generateId() ?: 0
                val todoItemNetwork = todoItem.mapToTodoItemNetwork(id.toString())
                withContext(Dispatchers.IO) {
                    val response =
                        todoItemsRepository.postTodoItemNetwork(
                            app.applicationContext,
                            SetItemRequest(todoItemNetwork = todoItemNetwork)
                        )

                    handlePostItemResponse(response)
                }
            } else Toast.makeText(app.applicationContext, "No internet connection", Toast.LENGTH_LONG).show()
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Toast.makeText(app.applicationContext, "Network Failure", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(app.applicationContext,"Conversion Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun generateId(): Int? {
        todoItemsRepository.todoItemsLiveData.value?.let { todoItems ->
            if (todoItems.isNotEmpty()) {
                return todoItems.size
            } else return 0
        }
        return null
    }

    private suspend fun handlePostItemResponse(response: Response<SetItemResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val newItem = resultResponse.todoItemNetwork.mapToTodoItem()
                todoItemsRepository.todoItemsLiveData.value?.let { todoItems ->
                    val revision = resultResponse.revision
                    SessionManager(app.applicationContext).saveRevision(revision)

                    val newList = todoItems.toMutableList()
                    newList.add(newItem)
                    todoItemsRepository.setTodoItemsLiveData(newList)
                }
            }
        } else withContext(Dispatchers.Main) { Toast.makeText(app.applicationContext, response.message(), Toast.LENGTH_LONG).show() }
    }

    fun putTodoItemNetwork(todoItem: TodoItem) = CoroutineScope(Dispatchers.Main).launch {
        putTodoItemSafeCall(todoItem)
    }

    private suspend fun putTodoItemSafeCall(todoItem: TodoItem) {
        try {
            if (hasInternetConnection()) {
                withContext(Dispatchers.IO) {
                    val todoItemNetwork = todoItem.mapToTodoItemNetwork(todoItem.id)
                    val response =
                        todoItemsRepository.putTodoItemNetwork(
                            app.applicationContext,
                            SetItemRequest(todoItemNetwork = todoItemNetwork)
                        )

                    handlePutItemResponse(response)
                }
            } else Toast.makeText(app.applicationContext, "No internet connection", Toast.LENGTH_LONG).show()

        } catch (t: Throwable) {
            when (t) {
                is IOException -> Toast.makeText(app.applicationContext, "Network Failure", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(app.applicationContext, "Conversion Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun handlePutItemResponse(response: Response<SetItemResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val newItem = resultResponse.todoItemNetwork.mapToTodoItem()
                todoItemsRepository.todoItemsLiveData.value?.let { todoItems ->
                    val revision = resultResponse.revision
                    SessionManager(app.applicationContext).saveRevision(revision)

                    val newList = todoItems.toMutableList()
                    todoItems.forEachIndexed { index, todoItem ->
                        if (todoItem.id.toInt() == newItem.id.toInt()) {
                            newList[index] = newItem
                            return@forEachIndexed
                        }
                    }
                    todoItemsRepository.setTodoItemsLiveData(newList.toList())
                }
            }
        } else withContext(Dispatchers.Main) { Toast.makeText(app.applicationContext, response.message(), Toast.LENGTH_LONG).show() }
    }

    fun deleteTodoItemNetwork(todoItem: TodoItem) = CoroutineScope(Dispatchers.Main).launch {
        deleteTodoItemSafeCall(todoItem)
    }

    private suspend fun deleteTodoItemSafeCall(todoItem: TodoItem) {
        try {
            if (hasInternetConnection()) {
                withContext(Dispatchers.IO) {
                    val todoItemNetwork = todoItem.mapToTodoItemNetwork(todoItem.id)
                    val response =
                        todoItemsRepository.deleteTodoItemNetwork(
                            app.applicationContext,
                            SetItemRequest(todoItemNetwork = todoItemNetwork)
                        )

                    handleDeleteItemResponse(response)
                }
            } else Toast.makeText(app.applicationContext, "No internet connection", Toast.LENGTH_LONG).show()

        } catch (t: Throwable) {
            when (t) {
                is IOException -> Toast.makeText(app.applicationContext, "Network Failure", Toast.LENGTH_LONG).show()
                else -> Toast.makeText(app.applicationContext, "Conversion Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun handleDeleteItemResponse(response: Response<SetItemResponse>) {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                val newItem = resultResponse.todoItemNetwork.mapToTodoItem()
                todoItemsRepository.todoItemsLiveData.value?.let { todoItems ->
                    val revision = resultResponse.revision
                    SessionManager(app.applicationContext).saveRevision(revision)

                    val newList = todoItems.toMutableList()
                    todoItems.forEachIndexed { index, todoItem ->
                        if (todoItem.id.toInt() == newItem.id.toInt()) {
                            newList.removeAt(index)
                            return@forEachIndexed
                        }
                    }
                    todoItemsRepository.setTodoItemsLiveData(newList)
                }
            }
        } else withContext(Dispatchers.Main) { Toast.makeText(app.applicationContext, response.message(), Toast.LENGTH_LONG).show() }
    }

//    private suspend fun generateId(): Int? {
//        val response = todoItemsRepository.getTodoItemsNetwork(app.applicationContext)
//        return handleGetItemResponse(response)
//    }

//    private fun handleGetItemResponse(response: Response<GetItemsResponse>): Int? {
//        if (response.isSuccessful) {
//            response.body()?.let { resultResponse ->
//                val revision = resultResponse.revision
//                SessionManager(app.applicationContext).saveRevision(revision)
//
//                return resultResponse.todoItemsNetwork.size
//            }
//        }
//        getTodoItemMessage = response.message()
//        return null
//    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<TodoApplication>().getSystemService(
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