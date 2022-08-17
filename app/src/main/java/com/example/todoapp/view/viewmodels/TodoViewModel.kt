package com.example.todoapp.view.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    var stateVisibility = StateVisibility.VISIBLE
    var todoItems = listOf<TodoItem>()

    fun getTodoItemsLiveData() = todoItemsRepository.getTodoItemsLivaData()

    fun getTodoItemsNetwork() =
        viewModelScope.launch {
            todoItemsRepository.getTodoItemsNetwork()
        }

    fun addTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.addTodoItem(todoItem, todoItem.id)
        }

    fun editTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.editTodoItem(todoItem)
        }

    fun deleteTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.deleteTodoItem(todoItem)
        }
}
