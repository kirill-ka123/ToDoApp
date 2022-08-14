package com.example.todoapp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    init {
        getRequestTodoItemsNetwork()
    }

    var stateVisibility = StateVisibility.VISIBLE

    fun getTodoItems() = getTodoItemsLiveData().value ?: emptyList()

    fun getTodoItemsLiveData() = todoItemsRepository.todoItemsLiveData

    fun getStateGetRequestLiveData() = todoItemsRepository.stateGetRequestLiveData

    fun getStateSetRequestLiveData() = todoItemsRepository.stateSetRequestLiveData

    fun getRequestTodoItemsNetwork() =
        viewModelScope.launch {
            todoItemsRepository.getTodoItemsNetwork()
        }

    fun postTodoItemNetwork(todoItem: TodoItem, id: String) =
        viewModelScope.launch {
            todoItemsRepository.postTodoItemNetwork(todoItem, id)
        }

    fun putTodoItemNetwork(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.putTodoItemNetwork(todoItem)
        }

    fun deleteTodoItemNetwork(id: String) =
        viewModelScope.launch {
            todoItemsRepository.deleteTodoItemNetwork(id)
        }
}