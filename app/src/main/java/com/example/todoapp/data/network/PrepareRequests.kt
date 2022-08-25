package com.example.todoapp.data.network

import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.TodoItemNetwork
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.data.network.models.UpdateItemRequest
import com.example.todoapp.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class PrepareRequests @Inject constructor() {
    fun prepareAddTodoItemRequest(
        todoItem: TodoItem,
        id: String?
    ): TodoItem {
        return if (id == null) {
            val newId = System.currentTimeMillis()
            todoItem.copy(id = newId.toString())
        } else todoItem
    }

    fun preparePostRequest(todoItem: TodoItem): UpdateItemRequest {
        val todoItemNetwork: TodoItemNetwork = todoItem.mapToTodoItemNetwork()
        return UpdateItemRequest(todoItemNetwork = todoItemNetwork)
    }

    fun preparePutRequest(todoItem: TodoItem): UpdateItemRequest {
        val todoItemNetwork: TodoItemNetwork = todoItem.mapToTodoItemNetwork()
        return UpdateItemRequest(todoItemNetwork = todoItemNetwork)
    }

    fun preparePatchRequest(todoItems: List<TodoItem>): SetItemsRequest {
        val todoItemsNetwork = todoItems.map { it.mapToTodoItemNetwork() }
        return SetItemsRequest(todoItemsNetwork = todoItemsNetwork)
    }
}