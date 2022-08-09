package com.example.todoapp.presentation.ioc

import android.app.Application
import com.example.todoapp.data.repository.TodoItemsRepository

class ApplicationComponent(app: Application)  {
    private val todoItemsRepository = TodoItemsRepository
    val todoViewModelFactory =  TodoViewModelFactory(app, todoItemsRepository)
    val caseViewModelFactory = CaseViewModelFactory(app, todoItemsRepository)
}