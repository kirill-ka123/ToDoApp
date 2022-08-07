package com.example.todoapp.ui.viewModels.todoViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.common.StateVisibility
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

    var stateVisibility = StateVisibility.VISIBLE

    fun getTodoItems() = todoItemsRepository.todoItemsLiveData.value ?: listOf()

    fun getTodoItemsLive() = todoItemsRepository.todoItemsLiveData

    fun getStateGetRequestLive() = todoItemsRepository.stateGetRequest

    fun getStateSetRequestLive() = todoItemsRepository.stateSetRequest

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