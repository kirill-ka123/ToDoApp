package com.example.todoapp.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.repository.TodoItemsRepository

class NetworkWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(
        context,
        workerParams
    ) {

    override suspend fun doWork(): Result {
        return try {
            TodoItemsRepository.getTodoItemsNetwork(context)
            Result.success()
        } catch (e: InterruptedException) {
            Result.failure()
        }
    }
}