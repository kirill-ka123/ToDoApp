package com.example.todoapp.presentation

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todoapp.data.network.NetworkWorker
import com.example.todoapp.presentation.di.AppComponent
import com.example.todoapp.presentation.di.AppModule
import com.example.todoapp.presentation.di.DaggerAppComponent
import com.example.todoapp.presentation.ioc.ApplicationComponent
import java.util.concurrent.TimeUnit

class TodoApplication : Application() {

    lateinit var appComponent: AppComponent

    val applicationComponent by lazy { ApplicationComponent(this) }

    companion object {
        fun get(context: Context): TodoApplication = context.applicationContext as TodoApplication
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
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
