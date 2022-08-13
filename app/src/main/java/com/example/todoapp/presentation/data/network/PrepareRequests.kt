package com.example.todoapp.presentation.data.network

import com.example.todoapp.presentation.data.network.models.SetItemRequest
import com.example.todoapp.presentation.data.network.models.TodoItemNetwork
import com.example.todoapp.presentation.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.presentation.models.TodoItem

class PrepareRequests {
    fun preparePostRequest(
        currentList: List<TodoItem>,
        todoItem: TodoItem,
        id: String?
    ): SetItemRequest {
        val todoItemNetwork: TodoItemNetwork = if (id != null) {
            todoItem.mapToTodoItemNetwork(id)
        } else todoItem.mapToTodoItemNetwork(
            generateId(currentList).toString()
        )
        return SetItemRequest(todoItemNetwork = todoItemNetwork)
    }

    private fun generateId(currentList: List<TodoItem>): Int {
        return if (currentList.isNotEmpty()) {
            currentList.last().id.toInt() + 1
        } else 0
    }

    fun preparePutRequest(todoItem: TodoItem): SetItemRequest {
        val todoItemNetwork: TodoItemNetwork = todoItem.mapToTodoItemNetwork(todoItem.id)
        return SetItemRequest(todoItemNetwork = todoItemNetwork)
    }
}