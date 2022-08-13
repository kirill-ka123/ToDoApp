package com.example.todoapp.presentation.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.presentation.data.repository.TodoItemsRepository
import com.example.todoapp.presentation.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class TodoViewModelFactory @Inject constructor(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(app, todoItemsRepository) as T
    }
}