package com.example.todoapp

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.network.NetworkWorker
import java.util.concurrent.TimeUnit

class TodoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val networkRequest = PeriodicWorkRequestBuilder<NetworkWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(networkRequest)
    }
}
