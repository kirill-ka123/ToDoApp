package com.example.todoapp.network.models

import com.google.gson.annotations.SerializedName

data class SetItemResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("element")
    val todoItemNetwork: TodoItemNetwork,
    @SerializedName("revision")
    val revision: Int
)
