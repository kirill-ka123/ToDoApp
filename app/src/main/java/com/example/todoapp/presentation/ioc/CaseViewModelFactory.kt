package com.example.todoapp.presentation.ioc

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.presentation.data.repository.TodoItemsRepository
import com.example.todoapp.presentation.view.CaseViewModel

class CaseViewModelFactory(
    private val app: Application,
    private val todoItemsRepository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CaseViewModel(app, todoItemsRepository) as T
    }
}