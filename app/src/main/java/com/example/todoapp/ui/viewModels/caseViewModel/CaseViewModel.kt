package com.example.todoapp.ui.viewModels.caseViewModel

import androidx.lifecycle.ViewModel
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CaseViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun saveTodoItem(todoItem: TodoItem) = scope.launch {
        todoItemsRepository.upsertTodoItem(todoItem)
    }

    fun deleteTodoItem(todoItem: TodoItem) = scope.launch {
        todoItemsRepository.deleteTodoItem(todoItem)
    }
}