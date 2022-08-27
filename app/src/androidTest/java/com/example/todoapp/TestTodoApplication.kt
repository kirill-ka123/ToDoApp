package com.example.todoapp

import com.example.todoapp.di.DaggerTestAppComponent

class TestTodoApplication : TodoApplication() {
    override fun onCreate() {
        super.onCreate()
        // Подменяю appComponent, который находится внутри TodoApplication на TestAppComponent
        // После этого внутри фрагментов будут инжектиться фальшивые модули
        appComponent = DaggerTestAppComponent.factory().create(this)
        appComponent.inject(this)
    }
}