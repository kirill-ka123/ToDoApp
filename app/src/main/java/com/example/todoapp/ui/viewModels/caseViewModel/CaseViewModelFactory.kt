package com.example.todoapp.ui.viewModels.caseViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.repository.TodoItemsRepository

class CaseViewModelFactory(private val todoItemsRepository: TodoItemsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CaseViewModel(todoItemsRepository) as T
    }
}