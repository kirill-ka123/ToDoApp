package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.common.FakeSessionManager
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.TodoItemsDatabase
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking

@Module
class FakeDatabaseModule {
    @Provides
    @AppScope
    fun provideTodoItemsDatabase(appContext: Context): TodoItemsDatabase {
        return Room.inMemoryDatabaseBuilder(
            appContext,
            TodoItemsDatabase::class.java
        ).build()
    }

    @Provides
    @AppScope
    fun provideTodoItemsDao(database: TodoItemsDatabase): TodoItemsDao {
        val testTodoItems = mutableListOf<TodoItem>()
        ('a'..'g').forEachIndexed { index, c ->
            val createdAt = (1L..100L).random()
            testTodoItems.add(
                TodoItem(
                    id = index.toString(),
                    text = c.toString(),
                    importance = Importance.IMPORTANT,
                    done = index % 2 == 1,
                    createdAt = createdAt,
                    changedAt = createdAt + 1
                )
            )
        }
        testTodoItems.shuffle()

        val databaseDao = database.getTodoItemsDao()
        runBlocking {
            databaseDao.addAllTodoItems(testTodoItems)
        }

        return databaseDao
    }

    @Provides
    @AppScope
    fun provideSessionManager(appContext: Context): SessionManager {
        return FakeSessionManager(appContext)
    }
}