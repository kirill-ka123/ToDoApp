package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem

class TodoItemsRepository {
    private val _todoItemsLiveData: MutableLiveData<MutableList<TodoItem>> = MutableLiveData(SourceData.todoItems)
    val todoItemsLiveData: LiveData<MutableList<TodoItem>> = _todoItemsLiveData

    fun getNumberOfTodoItems() = todoItemsLiveData.value?.size

    fun upsertTodoItem(updatedTodoItem: TodoItem) {
        _todoItemsLiveData.value?.forEachIndexed { index, todoItem ->
            if (todoItem.id == updatedTodoItem.id) {
                _todoItemsLiveData.value?.set(index, updatedTodoItem)
                return@upsertTodoItem
            }
        }
        _todoItemsLiveData.value?.add(updatedTodoItem)
    }

    fun deleteTodoItem(item: TodoItem) {
        _todoItemsLiveData.value?.forEachIndexed { index, todoItem ->
            if (todoItem.id == item.id) {
                _todoItemsLiveData.value?.removeAt(index)
                return@deleteTodoItem
            }
        }
    }
}