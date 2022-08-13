package com.example.todoapp.presentation.data.network.models

import com.google.gson.annotations.SerializedName

data class SetItemRequest (
    @SerializedName("status")
    val status: String = "ok",
    @SerializedName("element")
    val todoItemNetwork: TodoItemNetwork
)
