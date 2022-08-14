package com.example.todoapp.view.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class CaseViewModelFactory @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CaseViewModel(todoItemsRepository) as T
    }
}