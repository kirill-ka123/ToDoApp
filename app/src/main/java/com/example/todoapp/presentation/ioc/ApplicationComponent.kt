package com.example.todoapp.presentation.ioc

import android.app.Application
import com.example.todoapp.presentation.TodoApplication
import com.example.todoapp.presentation.data.network.PrepareRequests
import com.example.todoapp.presentation.data.repository.TodoItemsRepository

class ApplicationComponent(app: Application)  {
    private val todoItemsRepository = (app as TodoApplication).todoItemsRepository
    val todoViewModelFactory =  TodoViewModelFactory(app, todoItemsRepository)
    val caseViewModelFactory = CaseViewModelFactory(app, todoItemsRepository)
}