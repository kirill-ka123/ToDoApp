package com.example.todoapp.ioc

import androidx.fragment.app.Fragment
import com.example.todoapp.view.TodoAdapter
import com.example.todoapp.view.TodoItemDiffCalculator
import com.example.todoapp.view.TodoViewModel

class TodoFragmentComponent(
    val applicationComponent: ApplicationComponent,
    val fragment: Fragment,
    val viewModel: TodoViewModel
) {
    val adapter = TodoAdapter(TodoItemDiffCalculator())
}
