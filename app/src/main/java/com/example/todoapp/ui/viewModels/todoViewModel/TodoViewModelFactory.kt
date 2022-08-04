package com.example.todoapp.ui.viewModels.todoViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.repository.TodoItemsRepository

class TodoViewModelFactory(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) :
    ViewModelProvider.AndroidViewModelFactory(app) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(app, todoItemsRepository) as T
    }
}