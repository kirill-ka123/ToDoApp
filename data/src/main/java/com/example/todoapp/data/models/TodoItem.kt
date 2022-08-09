package com.example.todoapp.data.models

import java.io.Serializable

data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long = 0L,
    val done: Boolean,
    val createdAt: Long,
    val changedAt: Long = 0L
) : Serializable