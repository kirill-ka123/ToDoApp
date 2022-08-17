package com.example.todoapp.data.network.models

import com.google.gson.annotations.SerializedName

data class UpdateItemRequest (
    @SerializedName("status")
    val status: String = "ok",
    @SerializedName("element")
    val todoItemNetwork: TodoItemNetwork
)
