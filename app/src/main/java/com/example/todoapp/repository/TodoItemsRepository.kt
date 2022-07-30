package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem

class TodoItemsRepository {
    private val _todoItemsLiveData: MutableLiveData<MutableList<TodoItem>> =
        MutableLiveData(SourceData.todoItems)
    val todoItemsLiveData: LiveData<MutableList<TodoItem>> = _todoItemsLiveData

    fun getNumberOfTodoItems() = todoItemsLiveData.value?.size

    fun upsertTodoItem(newTodoItem: TodoItem) {
        _todoItemsLiveData.value?.let {
            val newList = it
            newList.forEachIndexed { index, todoItem ->
                if (todoItem.id == newTodoItem.id) {
                    newList[index] = newTodoItem
                    _todoItemsLiveData.value = newList
                    return@upsertTodoItem
                }
            }
            newList.add(newTodoItem)
            _todoItemsLiveData.value = newList
        }
    }

    fun deleteTodoItem(item: TodoItem) {
        _todoItemsLiveData.value?.let {
            val newList = it
            newList.forEachIndexed { index, todoItem ->
                if (todoItem.id == item.id) {
                    newList.removeAt(index)
                    _todoItemsLiveData.value = newList
                    return@deleteTodoItem
                }
            }
        }
    }
}