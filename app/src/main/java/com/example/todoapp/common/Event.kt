package com.example.todoapp.common

sealed class Event(
    var message: Int
) {
    class GetEvent(message: Int): Event(message)
    class SetEvent(message: Int): Event(message)
}