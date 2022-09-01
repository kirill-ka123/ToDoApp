package com.example.todoapp.di

import com.example.todoapp.di.scopes.CaseFragmentScope
import com.example.todoapp.presentation.view.screens.CaseFragment
import dagger.Subcomponent

@Subcomponent(modules = [CaseFragmentModule::class])
@CaseFragmentScope
interface CaseFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): CaseFragmentComponent
    }

    fun inject(caseFragment: CaseFragment)
}