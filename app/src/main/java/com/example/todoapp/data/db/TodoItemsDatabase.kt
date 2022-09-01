package com.example.todoapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoapp.data.db.models.TodoItem

@Database(
    version = 1,
    entities = [TodoItem::class]
)
abstract class TodoItemsDatabase : RoomDatabase() {
    abstract fun getTodoItemsDao(): TodoItemsDao
}