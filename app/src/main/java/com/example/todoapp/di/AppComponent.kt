package com.example.todoapp.di

import android.app.Application
import com.example.todoapp.TodoApplication
import com.example.todoapp.di.scopes.AppScope
import dagger.BindsInstance
import dagger.Component

@Component(modules = [AppModule::class])
@AppScope
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance app: Application
        ): AppComponent
    }

    fun inject(app: TodoApplication)
    fun todoFragmentComponent(): TodoFragmentComponent.Factory
    fun caseFragmentComponent(): CaseFragmentComponent.Factory
}