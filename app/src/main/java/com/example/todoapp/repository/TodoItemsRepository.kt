package com.example.todoapp.repository

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoItemsRepository(private val sourceData: MutableList<TodoItem>) {
    companion object {
        @Volatile
        private var instance: TodoItemsRepository? = null
        private val lock = Any()

        fun getRepository() = instance ?: synchronized(lock) {
            instance ?: TodoItemsRepository(SourceData.todoItems).also { instance = it }
        }
    }

    private val _todoItemsLiveData: MutableLiveData<List<TodoItem>> = MutableLiveData()
    val todoItemsLiveData: LiveData<List<TodoItem>> = _todoItemsLiveData

    private fun generateId() = sourceData.size.toString()

    suspend fun upsertTodoItem(newTodoItem: TodoItem) {
        withContext(Dispatchers.IO) {
            SystemClock.sleep(1000)

            if (newTodoItem.id == "") {
                newTodoItem.id = generateId()
                sourceData.add(newTodoItem)
            } else {
                var index = 0
                while (index < sourceData.size && newTodoItem.id.toInt() >= sourceData[index].id.toInt()) {
                    if (newTodoItem.id.toInt() == sourceData[index].id.toInt()) {
                        sourceData[index] = newTodoItem
                        _todoItemsLiveData.postValue(sourceData.toList())
                        return@withContext sourceData
                    }
                    index++
                }
                sourceData.add(index, newTodoItem)
            }
            _todoItemsLiveData.postValue(sourceData.toList())
        }
    }

    suspend fun deleteTodoItem(item: TodoItem) {
        withContext(Dispatchers.IO) {
            SystemClock.sleep(1000)

            var deleteIndex = 0
            sourceData.forEachIndexed { index, todoItem ->
                if (todoItem.id == item.id) {
                    deleteIndex = index
                    return@forEachIndexed
                }
            }
            sourceData.removeAt(deleteIndex)
            _todoItemsLiveData.postValue(sourceData.toList())
        }
    }

    suspend fun getTodoItems() {
        withContext(Dispatchers.IO) {
            SystemClock.sleep(1000)

            _todoItemsLiveData.postValue(sourceData.toList())
        }
    }
}