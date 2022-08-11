package com.example.todoapp.presentation.di

import android.app.Application
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.presentation.ioc.TodoViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class AppModule(val app: Application) {

    @Provides
    fun provideApplication(): Application {
        return app
    }

    @Provides
    fun provideTodoViewModelFactory(app: Application, todoItemsRepository: TodoItemsRepository): TodoViewModelFactory {
        return TodoViewModelFactory(app, todoItemsRepository)
    }
}