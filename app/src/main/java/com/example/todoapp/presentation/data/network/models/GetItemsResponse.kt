package com.example.todoapp.presentation.data.network.models

import com.google.gson.annotations.SerializedName

data class GetItemsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("list")
    val todoItemsNetwork: List<TodoItemNetwork>,
    @SerializedName("revision")
    val revision: Int
)