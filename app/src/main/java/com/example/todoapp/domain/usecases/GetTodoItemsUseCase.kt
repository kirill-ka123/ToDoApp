package com.example.todoapp.domain.usecases

import android.util.Log
import com.example.todoapp.common.Utils.callWithInternetCheck
import com.example.todoapp.common.Utils.callWithRetry
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.di.scopes.AppScope
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
        callWithRetry(call = {
            val getItemsResponse =
                callWithInternetCheck(checkInternet) { todoItemsRepository.getTodoItemsNetwork() }
            sessionManager.saveRevisionNetwork(getItemsResponse.revision)
            val todoItems = getListAfterGetRequest(getItemsResponse)
            synchronizationDataUseCase.synchronizeData(todoItems)
        }, actionAfterErroneousCall = {
            Log.e("network", "Request failure ${it.message}")
        })
    }

    private fun getListAfterGetRequest(body: GetItemsResponse) =
        body.todoItemsNetwork.map { it.mapToTodoItem() }
}