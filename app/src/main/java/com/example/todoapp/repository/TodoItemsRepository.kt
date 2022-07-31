package com.example.todoapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.models.TodoItem

class TodoItemsRepository(todoItems: MutableList<TodoItem>) {
    private val _todoItemsLiveData: MutableLiveData<MutableList<TodoItem>> =
        MutableLiveData(todoItems)
    val todoItemsLiveData: LiveData<MutableList<TodoItem>> = _todoItemsLiveData

    fun upsertTodoItem(newTodoItem: TodoItem) {
        val list = _todoItemsLiveData.value
        if (list != null) {
            val newList = list!!
            var index = 0
            while (index < newList.size && newTodoItem.id.toInt() >= newList[index].id.toInt()) {
                if (newTodoItem.id.toInt() == newList[index].id.toInt()) {
                    newList[index] = newTodoItem
                    _todoItemsLiveData.value = newList
                    return
                }
                index++
            }
            newList.add(index, newTodoItem)
            _todoItemsLiveData.value = newList
        } else {
            _todoItemsLiveData.value = mutableListOf(newTodoItem)
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

    fun getTodoItems() = _todoItemsLiveData.value?.toList()
}