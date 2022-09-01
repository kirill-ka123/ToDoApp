package com.example.todoapp.di

import android.app.Application
import com.example.todoapp.di.scopes.AppScope
import dagger.BindsInstance
import dagger.Component

@Component(modules = [FakeAppModule::class])
@AppScope
interface TestAppComponent : AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: Application
        ): TestAppComponent
    }
}