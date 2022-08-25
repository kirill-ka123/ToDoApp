package com.example.todoapp.presentation.view.viewmodels

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.models.StateNetwork
import com.example.todoapp.domain.usecases.GetTodoItemsUseCase
import com.example.todoapp.domain.usecases.UpdateTodoItemUseCase
import kotlinx.coroutines.launch

class TodoViewModel(
    private val getTodoItemsUseCase: GetTodoItemsUseCase,
    private val updateTodoItemUseCase: UpdateTodoItemUseCase
) : ViewModel() {
    var stateVisibility = StateVisibility.VISIBLE
    var todoItems = listOf<TodoItem>()

    private val _stateNetwork: MutableLiveData<StateNetwork> = MutableLiveData()
    val stateNetwork: LiveData<StateNetwork> = _stateNetwork

    fun getTodoItemsLiveData() = getTodoItemsUseCase.getTodoItemsLivaData()

    fun getTodoItemsNetwork() =
        viewModelScope.launch {
            getTodoItemsUseCase.getTodoItemsNetwork()
        }

    fun addTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            updateTodoItemUseCase.addTodoItem(todoItem, todoItem.id)
        }

    fun editTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            updateTodoItemUseCase.editTodoItem(todoItem)
        }

    fun deleteTodoItem(todoItem: TodoItem) =
        viewModelScope.launch {
            updateTodoItemUseCase.deleteTodoItem(todoItem)
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
            todoItem.createdAt
        }
}
