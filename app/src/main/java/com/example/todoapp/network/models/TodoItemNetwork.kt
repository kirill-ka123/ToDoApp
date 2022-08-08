package com.example.todoapp.network.models

import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.google.gson.annotations.SerializedName

data class TodoItemNetwork(
    @SerializedName("id")
    var id: String = "13",
    @SerializedName("text")
    val text: String = "text",
    @SerializedName("importance")
    val importance: String = "low",
    @SerializedName("deadline")
    val deadline: Long = 100000,
    @SerializedName("done")
    val done: Boolean = false,
    @SerializedName("color")
    val color: String = "#FFFFFF",
    @SerializedName("created_at")
    val createdAt: Long = 100000,
    @SerializedName("changed_at")
    val changedAt: Long = 100000,
    @SerializedName("last_updated_by")
    val lastUpdatedBy: String = "model"
) {
    fun mapToTodoItem() = TodoItem(
        id, text, Importance.valueOf(importance.uppercase()), deadline, done, createdAt, changedAt
    )

    companion object {
        fun TodoItem.mapToTodoItemNetwork(id: String) = TodoItemNetwork(
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
}