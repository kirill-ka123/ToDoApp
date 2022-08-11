package com.example.todoapp.presentation.di

import com.example.todoapp.data.repository.TodoItemsRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideTodoItemsRepository(): TodoItemsRepository {
        return TodoItemsRepository
    }
}