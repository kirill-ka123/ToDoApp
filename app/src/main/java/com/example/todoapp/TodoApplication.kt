package com.example.todoapp

import android.app.Application
import androidx.work.*
import com.example.todoapp.data.network.NetworkWorker
import com.example.todoapp.data.network.NetworkWorker.Companion.WORK_NAME
import com.example.todoapp.data.repository.TodoItemsRepository
import com.example.todoapp.di.AppComponent
import com.example.todoapp.di.DaggerAppComponent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

open class TodoApplication : Application() {
    open lateinit var appComponent: AppComponent

    @Inject
    lateinit var todoItemsRepository: TodoItemsRepository

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
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