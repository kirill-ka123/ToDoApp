package com.example.todoapp.common

sealed class StateRequest(
    val message: Int? = null
) {
    class Error(message: Int): StateRequest(message)
    class Success: StateRequest()
}