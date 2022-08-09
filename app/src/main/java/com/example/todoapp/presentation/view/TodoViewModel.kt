package com.example.todoapp.presentation.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.presentation.common.StateVisibility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    init {
        getTodoItemsNetwork()
    }

    var stateVisibility = StateVisibility.VISIBLE

    fun getTodoItems() = getTodoItemsLiveData().value ?: emptyList()

    fun getTodoItemsLiveData() = todoItemsRepository.todoItemsLiveData

    fun getStateGetRequestLiveData() = todoItemsRepository.stateGetRequestLiveData

    fun getStateSetRequestLiveData() = todoItemsRepository.stateSetRequestLiveData

    fun getTodoItemsNetwork() =
        viewModelScope.launch(Dispatchers.IO) {
            todoItemsRepository.getTodoItemsNetwork(app.applicationContext)
        }

    fun postTodoItemNetwork(todoItem: TodoItem, id: String) =
        viewModelScope.launch(Dispatchers.IO) {
            todoItemsRepository.postTodoItemNetwork(app.applicationContext, todoItem, id)
        }

    fun putTodoItemNetwork(todoItem: TodoItem) =
        viewModelScope.launch(Dispatchers.IO) {
            todoItemsRepository.putTodoItemNetwork(app.applicationContext, todoItem)
        }

    fun deleteTodoItemNetwork(id: String) =
        viewModelScope.launch(Dispatchers.IO) {
            todoItemsRepository.deleteTodoItemNetwork(app.applicationContext, id)
        }
}