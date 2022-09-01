package com.example.todoapp.domain.usecases

import android.util.Log
import com.example.todoapp.common.Utils.callWithInternetCheck
import com.example.todoapp.common.Utils.callWithRetry
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.di.scopes.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScope
class SynchronizationNetworkUseCase @Inject constructor(
    private val todoApi: TodoApi,
    private val sessionManager: SessionManager,
    private val checkInternet: CheckInternet
) {
    suspend fun synchronizeNetwork(todoItemsFromDatabase: List<TodoItem>) =
        withContext(Dispatchers.IO) {
            patchTodoItemsNetwork(todoItemsFromDatabase)
        }

    private suspend fun patchTodoItemsNetwork(todoItems: List<TodoItem>) {
        val setItemsRequest = preparePatchRequest(todoItems)

        callWithRetry(call = {
            patchTodoItemsWithInternetCheck(setItemsRequest)
        }, actionAfterErroneousCall = {
            Log.e("network", "Request failure: ${it.message}")
        })
    }

    private suspend fun patchTodoItemsWithInternetCheck(setItemsRequest: SetItemsRequest) {
        callWithInternetCheck(checkInternet) {
            val newRevision = sessionManager.fetchRevisionDatabase() - 1
            val patchItemsResponse =
                todoApi.patchTodoItem(newRevision.toString(), setItemsRequest)
            sessionManager.saveRevisionNetwork(patchItemsResponse.revision)
        }
    }

    private fun preparePatchRequest(todoItems: List<TodoItem>): SetItemsRequest {
        val todoItemsNetwork = todoItems.map { it.mapToTodoItemNetwork() }
        return SetItemsRequest(todoItemsNetwork = todoItemsNetwork)
    }
}