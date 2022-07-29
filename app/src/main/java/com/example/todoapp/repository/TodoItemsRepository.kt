package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem

class TodoItemsRepository {
    private val _todoItemsLiveData: MutableLiveData<MutableList<TodoItem>> = MutableLiveData(SourceData.todoItems)
    val todoItemsLiveData: LiveData<MutableList<TodoItem>> = _todoItemsLiveData

    fun getNumberOfTodoItems() = todoItemsLiveData.value?.size

    fun addNewTodoItem(case: TodoItem) {
        _todoItemsLiveData.value?.add(case)
    }
}