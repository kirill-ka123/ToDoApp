package com.example.todoapp.ui.viewModels.todoViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.launch

class TodoViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {
    init {
        getTodoItems()
    }

    var stateVisibility = StateVisibility.VISIBLE

    fun saveTodoItem(todoItem: TodoItem) = viewModelScope.launch {
        todoItemsRepository.upsertTodoItem(todoItem)
    }

    fun deleteTodoItem(todoItem: TodoItem) = viewModelScope.launch {
        todoItemsRepository.deleteTodoItem(todoItem)
    }

    fun getTodoItems() = viewModelScope.launch {
        todoItemsRepository.getTodoItems()
    }

    fun getTodoItemsLive() = todoItemsRepository.todoItemsLiveData
}