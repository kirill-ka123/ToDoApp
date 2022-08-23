package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.TodoItemsDatabase
import com.example.todoapp.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @Provides
    @AppScope
    fun provideTodoItemsDatabase(appContext: Context): TodoItemsDatabase {
        return Room.databaseBuilder(
            appContext,
            TodoItemsDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @AppScope
    fun provideTodoItemsDao(database: TodoItemsDatabase): TodoItemsDao {
        return database.getTodoItemsDao()
    }

    companion object {
        const val DATABASE_NAME = "todoitems_db.db"
    }
}