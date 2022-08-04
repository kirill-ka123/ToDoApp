package com.example.todoapp.common

sealed class CaseResource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : CaseResource<T>(data)
    class Error<T>(message: String?, data: T? = null) : CaseResource<T>(data, message)
    class Loading<T> : CaseResource<T>()
    class Null<T> : CaseResource<T>()
}