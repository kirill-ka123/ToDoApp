package com.example.todoapp.common

sealed class Event(
    val message: String
) {
    class GetEvent(message: String): Event(message)
    class SetEvent(message: String): Event(message)
}