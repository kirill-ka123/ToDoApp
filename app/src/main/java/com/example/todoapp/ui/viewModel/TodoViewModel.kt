package com.example.todoapp.ui.viewModel

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.todoapp.R
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import java.text.DateFormat
import java.util.*

class TodoViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {
    var visibleOrInvisible = "visible"

    fun saveTodoItem(todoItem: TodoItem) = todoItemsRepository.upsertTodoItem(todoItem)

    fun deleteTodoItem(todoItem: TodoItem) = todoItemsRepository.deleteTodoItem(todoItem)

    fun getTodoItemsLiveData() = todoItemsRepository.todoItemsLiveData

    fun getTodoItems() = todoItemsRepository.getTodoItems()
}