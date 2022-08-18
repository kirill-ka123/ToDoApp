package com.example.todoapp.view.viewmodels

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CaseViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun postTodoItemNetwork(todoItem: TodoItem) = scope.launch {
        todoItemsRepository.postTodoItemNetwork(todoItem, null)
    }

    fun putTodoItemNetwork(todoItem: TodoItem) = scope.launch {
        todoItemsRepository.putTodoItemNetwork(todoItem)
    }

    fun deleteTodoItemNetwork(id: String) = scope.launch {
        todoItemsRepository.deleteTodoItemNetwork(id)
    }
}