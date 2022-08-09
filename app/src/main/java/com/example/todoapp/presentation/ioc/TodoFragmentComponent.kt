package com.example.todoapp.presentation.ioc

import androidx.fragment.app.Fragment
import com.example.todoapp.presentation.view.TodoAdapter
import com.example.todoapp.presentation.view.TodoItemDiffCalculator
import com.example.todoapp.presentation.view.TodoViewModel

class TodoFragmentComponent(
    val applicationComponent: ApplicationComponent,
    val fragment: Fragment,
    val viewModel: TodoViewModel
) {
    val adapter = TodoAdapter(TodoItemDiffCalculator())
}
