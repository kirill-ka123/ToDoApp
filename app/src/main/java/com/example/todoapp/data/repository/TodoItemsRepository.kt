package com.example.todoapp.data.repository

import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.UpdateItemRequest
import dagger.Reusable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class TodoItemsRepository @Inject constructor(
    private val databaseDao: TodoItemsDao,
    private val todoApi: TodoApi
) {
    fun getTodoItemsLivaData() = databaseDao.getAllTodoItemsLive()

    suspend fun getTodoItemsDatabase() = withContext(Dispatchers.IO) {
        databaseDao.getAllTodoItems().sortedBy { todoItem ->
            todoItem.createdAt
        }
    }

    suspend fun getTodoItemsNetwork() = withContext(Dispatchers.IO) {
        todoApi.getTodoItems()
    }

    suspend fun addTodoItemDatabase(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        databaseDao.addTodoItem(todoItem)
    }

    suspend fun postTodoItemNetwork(updateItemRequest: UpdateItemRequest) =
        withContext(Dispatchers.IO) {
            todoApi.postTodoItem(updateItemRequest)
        }

    suspend fun editTodoItemDatabase(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        databaseDao.editTodoItem(todoItem)
    }

    suspend fun putTodoItemNetwork(id: String, updateItemRequest: UpdateItemRequest) =
        withContext(Dispatchers.IO) {
            todoApi.putTodoItem(id, updateItemRequest)
        }

    suspend fun deleteTodoItemDatabase(todoItem: TodoItem) = withContext(Dispatchers.IO) {
        databaseDao.deleteTodoItem(todoItem)
    }

    suspend fun deleteTodoItemNetwork(id: String) = withContext(Dispatchers.IO) {
        todoApi.deleteTodoItem(id)
    }
}