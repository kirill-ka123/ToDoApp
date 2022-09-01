package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class SynchronizationDatabaseUseCase @Inject constructor(
    private val databaseDao: TodoItemsDao,
    private val sessionManager: SessionManager
) {
    suspend fun synchronizeDatabase(
        todoItemsFromNetwork: List<TodoItem>,
        todoItemsFromDatabase: List<TodoItem>
    ) {
        if (todoItemsFromDatabase.isNotEmpty()) {
            databaseDao.deleteAllTodoItems()
        }
        val newList = mergeData(todoItemsFromNetwork, todoItemsFromDatabase)
        databaseDao.addAllTodoItems(newList)
        sessionManager.saveRevisionDatabase(sessionManager.fetchRevisionNetwork())
    }

    // Слияние данных происходит по принципу - в приоритете данные из сети, но если элемент
    // из локальной базы данных изменялся позже, чем элемент с таким же id из сети, тогда добавляем его
    private fun mergeData(
        todoItemsFromNetwork: List<TodoItem>,
        todoItemsFromDatabase: List<TodoItem>
    ): List<TodoItem> {
        val newList = mutableListOf<TodoItem>()
        todoItemsFromNetwork.forEach { todoItemFromNetwork ->
            val todoItem = todoItemsFromDatabase.find { todoItemFromDatabase ->
                todoItemFromNetwork.id == todoItemFromDatabase.id
            }
            if (todoItem == null) {
                newList.add(todoItemFromNetwork)
            } else if (todoItem.changedAt ?: 0 > todoItemFromNetwork.changedAt ?: 0) {
                newList.add(todoItem)
            } else newList.add(todoItemFromNetwork)
        }
        return newList
    }
}