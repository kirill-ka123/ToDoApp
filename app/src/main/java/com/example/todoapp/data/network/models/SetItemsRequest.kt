package com.example.todoapp.data.network.models

import com.google.gson.annotations.SerializedName

data class SetItemsRequest(
    @SerializedName("status")
    val status: String = "ok",
    @SerializedName("list")
    val todoItemsNetwork: List<TodoItemNetwork>
)
