package com.example.todoapp.ui.viewModels.caseViewModel

import androidx.lifecycle.ViewModel
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {

    fun saveTodoItem(todoItem: TodoItem) = CoroutineScope(Dispatchers.IO).launch {
        todoItemsRepository.upsertTodoItem(todoItem)
    }

    fun deleteTodoItem(todoItem: TodoItem) = CoroutineScope(Dispatchers.IO).launch {
        todoItemsRepository.deleteTodoItem(todoItem)
    }
}