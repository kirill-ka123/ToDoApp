package com.example.todoapp.presentation.view.viewmodels

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.domain.usecases.UpdateTodoItemUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CaseViewModel(
    private val updateTodoItemUseCase: UpdateTodoItemUseCase
) : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun addTodoItem(todoItem: TodoItem) =
        scope.launch {
            updateTodoItemUseCase.addTodoItem(todoItem, null)
        }

    fun editTodoItem(todoItem: TodoItem) =
        scope.launch {
            updateTodoItemUseCase.editTodoItem(todoItem)
        }

    fun deleteTodoItem(todoItem: TodoItem) =
        scope.launch {
            updateTodoItemUseCase.deleteTodoItem(todoItem)
        }
}