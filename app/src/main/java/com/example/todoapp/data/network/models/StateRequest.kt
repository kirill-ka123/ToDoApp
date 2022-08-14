package com.example.todoapp.data.network.models

sealed class StateRequest(
    val error: Throwable? = null
) {
    class Error(error: Throwable): StateRequest(error)
    class Success: StateRequest()
}