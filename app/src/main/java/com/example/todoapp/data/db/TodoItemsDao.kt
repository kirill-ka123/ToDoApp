package com.example.todoapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.data.db.models.TodoItem

@Dao
interface TodoItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodoItem(todoItem: TodoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllTodoItems(todoItems: List<TodoItem>)

    @Update
    suspend fun editTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("SELECT * FROM todoItems")
    fun getAllTodoItemsLive(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM todoItems")
    suspend fun getAllTodoItems(): List<TodoItem>

    @Query("DELETE FROM todoItems")
    suspend fun deleteAllTodoItems()
}