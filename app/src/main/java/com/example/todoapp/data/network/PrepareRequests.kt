package com.example.todoapp.data.network

import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.TodoItemNetwork
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.data.network.models.UpdateItemRequest
import com.example.todoapp.di.scopes.AppScope
import com.example.todoapp.models.TodoItem
import javax.inject.Inject

@AppScope
class PrepareRequests @Inject constructor() {
    private fun generateId(todoItems: List<TodoItem>): Int {
        return if (todoItems.isNotEmpty()) {
            todoItems.last().id + 1
        } else 0
    }

    fun prepareAddTodoItemRequest(
        todoItem: TodoItem,
        id: Int?,
        todoItems: List<TodoItem>
    ): TodoItem {
        return if (id == null) {
            val newId = generateId(todoItems)
            todoItem.copy(id = newId)
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