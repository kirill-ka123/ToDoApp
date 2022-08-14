package com.example.todoapp.data.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todoapp.TodoApplication

class NetworkWorker(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(
        context,
        workerParams
    ) {

    override suspend fun doWork(): Result {
        return try {
            val todoItemsRepository = (context.applicationContext as TodoApplication).todoItemsRepository
            todoItemsRepository.getTodoItemsNetwork()
            Result.success()
        } catch (e: InterruptedException) {
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "Periodic Worker"
    }
}