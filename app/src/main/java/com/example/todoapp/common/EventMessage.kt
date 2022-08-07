package com.example.todoapp.common

sealed class EventMessage(
    val message: String
) {
    class GetEventMessage(message: String): EventMessage(message)
    class SetEventMessage(message: String): EventMessage(message)
}