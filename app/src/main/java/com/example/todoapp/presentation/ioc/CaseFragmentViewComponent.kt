package com.example.todoapp.presentation.ioc

import android.view.View
import com.example.todoapp.presentation.view.CaseFragment
import com.example.todoapp.presentation.view.CaseViewController

class CaseFragmentViewComponent(
    fragmentComponent: CaseFragmentComponent,
    root: View
) {
    val caseViewController = CaseViewController(
        fragmentComponent.fragment as CaseFragment,
        root,
        fragmentComponent.viewModel,
        fragmentComponent.args
    )
}