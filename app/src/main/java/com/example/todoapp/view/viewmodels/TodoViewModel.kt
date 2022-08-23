package com.example.todoapp.view.viewmodels

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.data.network.models.StateNetwork
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.models.TodoItem
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    var stateVisibility = StateVisibility.VISIBLE
    var todoItems = listOf<TodoItem>()

    private val _stateNetwork: MutableLiveData<StateNetwork> = MutableLiveData()
    val stateNetwork: LiveData<StateNetwork> = _stateNetwork

    fun getTodoItemsLiveData(): LiveData<List<TodoItem>> {
        return todoItemsRepository.getTodoItemsLivaData()
    }

    fun getTodoItemsNetwork() =
        viewModelScope.launch {
            todoItemsRepository.getTodoItemsNetwork()
        }

    fun addTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.addTodoItem(todoItem, todoItem.id)
        }

    fun editTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.editTodoItem(todoItem)
        }

    fun deleteTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            todoItemsRepository.deleteTodoItem(todoItem)
        }

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            getTodoItemsNetwork()
            _stateNetwork.postValue(StateNetwork.AVAILABLE)
        }

        override fun onLost(network: Network) {
            _stateNetwork.postValue(StateNetwork.LOST)
        }
    }

    fun sortTodoItems(todoItems: List<TodoItem>) =
        todoItems.sortedBy { todoItem ->
            try {
                todoItem.id.toInt()
            } catch (e: Exception) {
                0
            }
        }
}

