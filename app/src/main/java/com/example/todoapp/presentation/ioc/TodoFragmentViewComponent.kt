package com.example.todoapp.presentation.ioc

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.example.todoapp.presentation.view.TodoFragment
import com.example.todoapp.presentation.view.TodoViewController

class TodoFragmentViewComponent(
    fragmentComponent: TodoFragmentComponent,
    root: View,
    lifecycleOwner: LifecycleOwner
) {
    val todoViewController = TodoViewController(
        fragmentComponent.fragment as TodoFragment,
        root,
        lifecycleOwner,
        fragmentComponent.viewModel,
        fragmentComponent.adapter
    )
}
