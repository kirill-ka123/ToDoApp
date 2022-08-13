package com.example.todoapp.presentation.data.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.presentation.TodoApplication
import com.example.todoapp.presentation.data.repository.TodoItemsRepository

class NetworkWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(
        context,
        workerParams
    ) {

    override suspend fun doWork(): Result {
        return try {
            val todoItemsRepository = (context.applicationContext as TodoApplication).todoItemsRepository
            todoItemsRepository.getTodoItemsNetwork(applicationContext)
            Result.success()
        } catch (e: InterruptedException) {
            Result.failure()
        }
    }
}