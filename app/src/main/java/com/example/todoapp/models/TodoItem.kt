package com.example.todoapp.models

import java.io.Serializable

data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long? = null,
    val done: Boolean,
    val created_at: Long,
    val changed_at: Long? = null
) : Serializable