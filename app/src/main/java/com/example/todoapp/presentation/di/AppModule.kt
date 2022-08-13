package com.example.todoapp.presentation.di

import android.app.Application
import android.content.Context
import com.example.todoapp.presentation.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module(
    includes = [NetworkModule::class],
    subcomponents = [TodoFragmentComponent::class, CaseFragmentComponent::class]
)
class AppModule {
    @Provides
    @AppScope
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }
}