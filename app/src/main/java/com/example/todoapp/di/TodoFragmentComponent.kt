package com.example.todoapp.di

import com.example.todoapp.di.scopes.TodoFragmentScope
import com.example.todoapp.view.screens.TodoFragment
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