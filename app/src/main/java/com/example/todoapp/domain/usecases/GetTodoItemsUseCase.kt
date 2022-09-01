package com.example.todoapp.domain.usecases

import android.util.Log
import com.example.todoapp.common.Utils.callWithInternetCheck
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.di.scopes.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@AppScope
class GetTodoItemsUseCase @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    private val sessionManager: SessionManager,
    private val checkInternet: CheckInternet,
    private val synchronizationDataUseCase: SynchronizationDataUseCase
) {
    fun getTodoItemsLivaData() = todoItemsRepository.getTodoItemsLivaData()

    suspend fun getTodoItemsNetwork() {
        getTodoItemsInFlow().catch {
            Log.e("network", "Request failure ${it.message}")
        }.collect { todoItems ->
            synchronizationDataUseCase.synchronizeData(todoItems)
        }
    }

    private suspend fun getTodoItemsInFlow(): Flow<List<TodoItem>> {
        return flow {
            val getItemsResponse =
                callWithInternetCheck(checkInternet) { todoItemsRepository.getTodoItemsNetwork() }
            sessionManager.saveRevisionNetwork(getItemsResponse.revision)
            emit(getListAfterGetRequest(getItemsResponse))
        }.flowOn(Dispatchers.IO)
    }

    private fun getListAfterGetRequest(body: GetItemsResponse) =
        body.todoItemsNetwork.map { it.mapToTodoItem() }
}