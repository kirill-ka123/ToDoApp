package com.example.todoapp.data.network.models

import com.google.gson.annotations.SerializedName

data class UpdateItemResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val todoItemNetwork: TodoItemNetwork,
    @SerializedName("revision")
    val revision: Int
)
