package com.example.todoapp.ui.viewModels.todoViewModel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : AndroidViewModel(app) {
    init {
        getTodoItemsNetwork()
    }

    var visibleOrInvisible = "visible"

    private suspend fun repeatRequest(call: suspend () -> Unit) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.postDelayed({
            viewModelScope.launch { call() }
            mainHandler.removeCallbacksAndMessages(null)
        }, 10)
    }

    fun getTodoItemsLive() = todoItemsRepository.todoItemsLiveData

    fun getMessageLive() = todoItemsRepository.message

    fun refreshLiveData() {
        todoItemsRepository.refreshLiveData()
    }

    fun getTodoItemsNetwork() = viewModelScope.launch(Dispatchers.IO) {
        todoItemsRepository.getTodoItemsNetwork(app.applicationContext)
    }

    fun postTodoItemNetwork(todoItem: TodoItem, id: String) =
        viewModelScope.launch(Dispatchers.IO) {
            todoItemsRepository.postTodoItemNetwork(app.applicationContext, todoItem, id)
        }

    fun putTodoItemNetwork(todoItem: TodoItem) = viewModelScope.launch(Dispatchers.IO) {
        todoItemsRepository.putTodoItemNetwork(app.applicationContext, todoItem)
    }

    fun deleteTodoItemNetwork(id: String) = viewModelScope.launch(Dispatchers.IO) {
        todoItemsRepository.deleteTodoItemNetwork(app.applicationContext, id)
    }
}