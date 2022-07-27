package com.example.todoapp.models

import java.util.*

data class TodoItem(
    val id: UUID,
    val text: String,
    val importance: Importance,
    val deadline: Long?,
    val done: Boolean,
    val created_at: Long,
    val changed_at: Long?
)