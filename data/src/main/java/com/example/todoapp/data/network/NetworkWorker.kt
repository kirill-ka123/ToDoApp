package com.example.todoapp.data.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.data.repository.TodoItemsRepository

class NetworkWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(
        context,
        workerParams
    ) {

    override suspend fun doWork(): Result {
        return try {
            TodoItemsRepository.getTodoItemsNetwork(applicationContext)
            Result.success()
        } catch (e: InterruptedException) {
            Result.failure()
        }
    }
}