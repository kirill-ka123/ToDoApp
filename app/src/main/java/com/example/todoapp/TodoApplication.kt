package com.example.todoapp

import android.app.Application
import android.content.Context
import androidx.work.*
import com.example.todoapp.data.network.HandleResponses
import com.example.todoapp.data.network.NetworkWorker
import com.example.todoapp.data.network.NetworkWorker.Companion.WORK_NAME
import com.example.todoapp.data.network.PrepareRequests
import com.example.todoapp.data.network.SessionManager
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.ioc.ApplicationComponent
import java.util.concurrent.TimeUnit

class TodoApplication : Application() {

    val applicationComponent by lazy { ApplicationComponent(this) }

    lateinit var todoItemsRepository: TodoItemsRepository

    companion object {
        fun get(context: Context): TodoApplication = context.applicationContext as TodoApplication
    }

    override fun onCreate() {
        super.onCreate()
        todoItemsRepository = TodoItemsRepository.getRepository(
            applicationContext,
            PrepareRequests(),
            HandleResponses(SessionManager(applicationContext))
        )
        setupWorker()
    }

    private fun setupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val networkRequest = PeriodicWorkRequestBuilder<NetworkWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            networkRequest
        )
    }
}
