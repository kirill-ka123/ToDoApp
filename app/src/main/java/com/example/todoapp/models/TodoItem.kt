package com.example.todoapp.models

import java.io.Serializable
import java.util.UUID

data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long?,
    val done: Boolean,
    val created_at: Long,
    val changed_at: Long?
) : Serializable