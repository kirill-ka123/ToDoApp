package com.example.todoapp.models

import com.example.todoapp.network.models.TodoItemNetwork
import java.io.Serializable

data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long = 0L,
    val done: Boolean,
    val createdAt: Long,
    val changedAt: Long = 0L
) : Serializable {
    fun mapToTodoItemNetwork(id: String) = TodoItemNetwork(
        id,
        text,
        importance.name.lowercase(),
        deadline,
        done,
        "#FFFFFF",
        createdAt,
        changedAt,
        "model"
    )
}