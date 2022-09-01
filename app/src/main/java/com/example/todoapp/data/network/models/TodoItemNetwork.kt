package com.example.todoapp.data.network.models

import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.google.gson.annotations.SerializedName

data class TodoItemNetwork(
    @SerializedName("id")
    var id: String = "0",
    @SerializedName("text")
    val text: String? = "",
    @SerializedName("importance")
    val importance: String? = "no",
    @SerializedName("deadline")
    val deadline: Long? = 0,
    @SerializedName("done")
    val done: Boolean? = false,
    @SerializedName("color")
    val color: String? = "#FFFFFF",
    @SerializedName("created_at")
    val createdAt: Long? = 0,
    @SerializedName("changed_at")
    val changedAt: Long? = 0,
    @SerializedName("last_updated_by")
    val lastUpdatedBy: String? = "model"
) {
    fun mapToTodoItem() = TodoItem(
        id, text,
        try {
            importance?.let {
                Importance.valueOf(it.uppercase())
            }
        } catch (e: Exception) {
            Importance.BASIC
        }, deadline, done, createdAt, changedAt
    )

    companion object {
        fun TodoItem.mapToTodoItemNetwork() = TodoItemNetwork(
            id,
            text,
            importance?.name?.lowercase(),
            deadline,
            done,
            "#FFFFFF",
            createdAt,
            changedAt,
            "model"
        )
    }
}