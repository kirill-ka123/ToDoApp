package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class SynchronizationDataUseCase @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository,
    private val sessionManager: SessionManager,
    private val synchronizationNetworkUseCase: SynchronizationNetworkUseCase,
    private val synchronizationDatabaseUseCase: SynchronizationDatabaseUseCase,
) {
    suspend fun synchronizeData(todoItemsFromNetwork: List<TodoItem>) {
        val revisionNetwork = sessionManager.fetchRevisionNetwork()
        val revisionDatabase = sessionManager.fetchRevisionDatabase()

        if (revisionNetwork > revisionDatabase) {
            synchronizationDatabaseUseCase.synchronizeDatabase(
                todoItemsFromNetwork,
                todoItemsRepository.getTodoItemsDatabase()
            )
        } else {
            synchronizationNetworkUseCase.synchronizeNetwork(todoItemsRepository.getTodoItemsDatabase())
        }
    }
}