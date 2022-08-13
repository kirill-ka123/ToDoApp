package com.example.todoapp.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TodoItem(
    val id: String,
    val text: String,
    val importance: Importance,
    val deadline: Long = 0L,
    val done: Boolean,
    val createdAt: Long,
    val changedAt: Long = 0L
) : Parcelable