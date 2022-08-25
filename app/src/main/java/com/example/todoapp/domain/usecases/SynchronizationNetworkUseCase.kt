package com.example.todoapp.domain.usecases

import android.util.Log
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.di.scopes.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@AppScope
class SynchronizationNetworkUseCase @Inject constructor(
    private val todoApi: TodoApi,
    private val sessionManager: SessionManager,
    private val checkInternet: CheckInternet
) {
    private suspend fun <T> callWithInternetCheck(
        call: suspend () -> (T)
    ): T {
        if (checkInternet.hasInternetConnection()) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    suspend fun synchronizeNetwork(todoItemsFromDatabase: List<TodoItem>) {
        patchTodoItemsNetwork(todoItemsFromDatabase)
    }

    private suspend fun patchTodoItemsNetwork(todoItems: List<TodoItem>) =
        withContext(Dispatchers.IO) {
            val setItemsRequest = preparePatchRequest(todoItems)

            patchTodoItemsInFlow(setItemsRequest).retry(1) {
                delay(NETWORK_RETRY_DELAY)
                return@retry true
            }.catch {
                Log.e("network", "Request failure ${it.message}")
            }.collect()
        }

    private fun patchTodoItemsInFlow(
        setItemsRequest: SetItemsRequest
    ): Flow<GetItemsResponse> {
        return flow {
            callWithInternetCheck {
                val newRevision = sessionManager.fetchRevisionDatabase() - 1
                val patchItemsResponse =
                    todoApi.patchTodoItem(newRevision.toString(), setItemsRequest)
                sessionManager.saveRevisionNetwork(patchItemsResponse.revision)
                emit(patchItemsResponse)
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun preparePatchRequest(todoItems: List<TodoItem>): SetItemsRequest {
        val todoItemsNetwork = todoItems.map { it.mapToTodoItemNetwork() }
        return SetItemsRequest(todoItemsNetwork = todoItemsNetwork)
    }

    companion object {
        const val NETWORK_RETRY_DELAY = 1000L
    }
}