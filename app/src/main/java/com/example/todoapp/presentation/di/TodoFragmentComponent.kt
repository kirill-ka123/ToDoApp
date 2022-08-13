package com.example.todoapp.presentation.di

import com.example.todoapp.presentation.di.scopes.TodoFragmentScope
import com.example.todoapp.presentation.view.screens.TodoFragment
import dagger.Subcomponent

@Subcomponent(modules = [TodoFragmentModule::class])
@TodoFragmentScope
interface TodoFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoFragmentComponent
    }

    fun inject(todoFragment: TodoFragment)
}