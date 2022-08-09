package com.example.todoapp.data.network.preparing

import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.models.SetItemRequest
import com.example.todoapp.data.network.models.TodoItemNetwork
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork

object PreparePostRequest {
    fun invoke(currentList: List<TodoItem>, todoItem: TodoItem, id: String?): SetItemRequest {
        val todoItemNetwork: TodoItemNetwork = if (id != null) {
            todoItem.mapToTodoItemNetwork(id)
        } else todoItem.mapToTodoItemNetwork(
            generateId(currentList).toString()
        )
        return SetItemRequest(todoItemNetwork = todoItemNetwork)
    }

    private fun generateId(currentList: List<TodoItem>): Int {
        if (currentList.isNotEmpty()) {
            return currentList.last().id.toInt() + 1
        } else return 0
    }
}