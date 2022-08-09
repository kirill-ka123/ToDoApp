package com.example.todoapp.presentation.ioc

import androidx.fragment.app.Fragment
import com.example.todoapp.presentation.view.CaseFragmentArgs
import com.example.todoapp.presentation.view.CaseViewModel

class CaseFragmentComponent(
    val applicationComponent: ApplicationComponent,
    val fragment: Fragment,
    val viewModel: CaseViewModel,
    val args: CaseFragmentArgs
)