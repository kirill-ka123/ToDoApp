package com.example.todoapp.ui.viewModels.caseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseViewModel(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : AndroidViewModel(app) {
    fun postTodoItemNetwork(todoItem: TodoItem) = CoroutineScope(Dispatchers.Main).launch {
        todoItemsRepository.postTodoItemNetwork(app.applicationContext, todoItem, null)
    }

    fun putTodoItemNetwork(todoItem: TodoItem) = CoroutineScope(Dispatchers.Main).launch {
        todoItemsRepository.putTodoItemNetwork(app.applicationContext, todoItem)
    }

    fun deleteTodoItemNetwork(id: String) = CoroutineScope(Dispatchers.IO).launch {
        todoItemsRepository.deleteTodoItemNetwork(app.applicationContext, id)
    }
}