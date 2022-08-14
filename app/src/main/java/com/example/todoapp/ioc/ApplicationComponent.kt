package com.example.todoapp.ioc

import android.app.Application
import com.example.todoapp.TodoApplication

class ApplicationComponent(app: Application)  {
    private val todoItemsRepository = (app as TodoApplication).todoItemsRepository
    val todoViewModelFactory =  TodoViewModelFactory(todoItemsRepository)
    val caseViewModelFactory = CaseViewModelFactory(todoItemsRepository)
}