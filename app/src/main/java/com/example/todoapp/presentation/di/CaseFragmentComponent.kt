package com.example.todoapp.presentation.di

import com.example.todoapp.presentation.di.scopes.CaseFragmentScope
import com.example.todoapp.presentation.view.CaseFragment
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