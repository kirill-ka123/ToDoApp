package com.example.todoapp.presentation.di

import com.example.todoapp.presentation.view.CaseFragment
import com.example.todoapp.presentation.view.TodoFragment
import dagger.Component

@Component(modules = [AppModule::class, RepositoryModule::class])
interface AppComponent {
    fun inject(todoFragment: TodoFragment)
    fun inject(caseFragment: CaseFragment)
}