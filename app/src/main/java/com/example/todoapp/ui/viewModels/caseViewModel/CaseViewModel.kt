package com.example.todoapp.ui.viewModels.caseViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.launch

class CaseViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {

    fun saveTodoItem(todoItem: TodoItem) = viewModelScope.launch {
        todoItemsRepository.upsertTodoItem(todoItem)
    }

    fun deleteTodoItem(todoItem: TodoItem) = viewModelScope.launch {
        todoItemsRepository.deleteTodoItem(todoItem)
    }
}