package com.example.todoapp.presentation.ioc

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.presentation.view.TodoViewModel

class TodoViewModelFactory(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(app, todoItemsRepository) as T
    }
}