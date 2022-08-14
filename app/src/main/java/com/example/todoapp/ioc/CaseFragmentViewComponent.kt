package com.example.todoapp.ioc

import android.view.View
import com.example.todoapp.view.CaseFragment
import com.example.todoapp.view.CaseViewController

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