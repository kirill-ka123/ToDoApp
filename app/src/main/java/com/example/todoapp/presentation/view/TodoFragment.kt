package com.example.todoapp.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import com.example.todoapp.presentation.TodoApplication
import com.example.todoapp.presentation.ioc.TodoFragmentComponent
import com.example.todoapp.presentation.ioc.TodoFragmentViewComponent
import com.example.todoapp.presentation.ioc.TodoViewModelFactory
import javax.inject.Inject

class TodoFragment : Fragment(R.layout.todo_fragment) {
    private val applicationComponent
        get() = TodoApplication.get(requireContext()).applicationComponent
    private lateinit var fragmentComponent: TodoFragmentComponent
    private var fragmentViewComponent: TodoFragmentViewComponent? = null

    @Inject
    lateinit var vmFactory: TodoViewModelFactory

    private val todoViewModel: TodoViewModel by viewModels {
        vmFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireContext().applicationContext as TodoApplication).appComponent.inject(this)

        fragmentComponent = TodoFragmentComponent(
            applicationComponent,
            fragment = this,
            viewModel = todoViewModel,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewComponent = TodoFragmentViewComponent(
            fragmentComponent,
            root = view,
            lifecycleOwner = viewLifecycleOwner,
        ).apply {
            todoViewController.apply {
                setupViews()
                setupObservers()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentViewComponent = null
    }
}