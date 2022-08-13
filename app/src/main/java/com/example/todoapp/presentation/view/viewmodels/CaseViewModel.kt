package com.example.todoapp.presentation.view.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.todoapp.presentation.models.TodoItem
import com.example.todoapp.presentation.data.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CaseViewModel(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun postTodoItemNetwork(todoItem: TodoItem) = scope.launch {
        todoItemsRepository.postTodoItemNetwork(app.applicationContext, todoItem, null)
    }

    fun putTodoItemNetwork(todoItem: TodoItem) = scope.launch {
        todoItemsRepository.putTodoItemNetwork(app.applicationContext, todoItem)
    }

    fun deleteTodoItemNetwork(id: String) = scope.launch {
        todoItemsRepository.deleteTodoItemNetwork(app.applicationContext, id)
    }
}