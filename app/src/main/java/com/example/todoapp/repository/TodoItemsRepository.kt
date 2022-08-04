package com.example.todoapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todoapp.data.SourceData
import com.example.todoapp.models.TodoItem
import com.example.todoapp.network.RetrofitInstance
import com.example.todoapp.network.models.SetItemRequest

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

    fun setTodoItemsLiveData(todoItems: List<TodoItem>) {
        _todoItemsLiveData.postValue(todoItems)
    }
//
//    fun getTodoItemsLiveData() = _todoItemsLiveData
//
//    private fun generateId() = sourceData.size.toString()
//
//    suspend fun upsertTodoItem(newTodoItem: TodoItem) {
//        withContext(Dispatchers.IO) {
//            SystemClock.sleep(1000)
//
//            if (newTodoItem.id == "") {
//                newTodoItem.id = generateId()
//                sourceData.add(newTodoItem)
//            } else {
//                var index = 0
//                while (index < sourceData.size && newTodoItem.id.toInt() >= sourceData[index].id.toInt()) {
//                    if (newTodoItem.id.toInt() == sourceData[index].id.toInt()) {
//                        sourceData[index] = newTodoItem
//                        _todoItemsLiveData.postValue(sourceData.toList())
//                        return@withContext sourceData
//                    }
//                    index++
//                }
//                sourceData.add(index, newTodoItem)
//            }
//            _todoItemsLiveData.postValue(sourceData.toList())
//        }
//    }
//
//    suspend fun deleteTodoItem(item: TodoItem) {
//        withContext(Dispatchers.IO) {
//            SystemClock.sleep(1000)
//
//            var deleteIndex = 0
//            sourceData.forEachIndexed { index, todoItem ->
//                if (todoItem.id == item.id) {
//                    deleteIndex = index
//                    return@forEachIndexed
//                }
//            }
//            sourceData.removeAt(deleteIndex)
//            _todoItemsLiveData.postValue(sourceData.toList())
//        }
//    }
//
//    suspend fun getTodoItems() {
//        withContext(Dispatchers.IO) {
//            SystemClock.sleep(1000)
//
//            _todoItemsLiveData.postValue(sourceData.toList())
//        }
//    }

    suspend fun getTodoItemsNetwork(context: Context) =
        RetrofitInstance.getApi(context).getTodoItems()

    suspend fun getTodoItemByIdNetwork(context: Context, id: String) =
        RetrofitInstance.getApi(context).getTodoItemById(id)

    suspend fun postTodoItemNetwork(context: Context, setItemRequest: SetItemRequest) =
        RetrofitInstance.getApi(context).postTodoItem(setItemRequest)

    suspend fun putTodoItemNetwork(context: Context, setItemRequest: SetItemRequest) =
        RetrofitInstance.getApi(context)
            .putTodoItem(setItemRequest.todoItemNetwork.id, setItemRequest)

    suspend fun deleteTodoItemNetwork(context: Context, setItemRequest: SetItemRequest) =
        RetrofitInstance.getApi(context).deleteTodoItem(setItemRequest.todoItemNetwork.id)
}