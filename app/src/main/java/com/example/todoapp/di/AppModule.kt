package com.example.todoapp.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.example.todoapp.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module(
    includes = [NetworkModule::class, DatabaseModule::class],
    subcomponents = [TodoFragmentComponent::class, CaseFragmentComponent::class]
)
class AppModule {
    @Provides
    @AppScope
    fun provideAppContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @AppScope
    fun provideConnectivityManager(appContext: Context): ConnectivityManager {
        return appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}