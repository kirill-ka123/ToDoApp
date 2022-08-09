package com.example.todoapp.data.network.preparing

import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.models.SetItemRequest
import com.example.todoapp.data.network.models.TodoItemNetwork
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork

object PreparePutRequest {
    fun invoke(todoItem: TodoItem): SetItemRequest {
        val todoItemNetwork: TodoItemNetwork = todoItem.mapToTodoItemNetwork(todoItem.id)
        return SetItemRequest(todoItemNetwork = todoItemNetwork)
    }
}