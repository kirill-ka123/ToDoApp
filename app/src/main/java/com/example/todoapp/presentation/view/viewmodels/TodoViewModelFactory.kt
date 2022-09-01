package com.example.todoapp.presentation.view.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.di.scopes.AppScope
import com.example.todoapp.domain.usecases.GetTodoItemsUseCase
import com.example.todoapp.domain.usecases.UpdateTodoItemUseCase
import javax.inject.Inject

@AppScope
class TodoViewModelFactory @Inject constructor(
    private val getTodoItemsUseCase: GetTodoItemsUseCase,
    private val updateTodoItemUseCase: UpdateTodoItemUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TodoViewModel(getTodoItemsUseCase, updateTodoItemUseCase) as T
    }
}