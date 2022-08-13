package com.example.todoapp.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import com.example.todoapp.presentation.TodoApplication
import com.example.todoapp.presentation.di.TodoFragmentComponent
import javax.inject.Inject

class TodoFragment : Fragment(R.layout.todo_fragment) {
    private lateinit var todoFragmentComponent: TodoFragmentComponent

    @Inject
    lateinit var todoViewControllerFactory: TodoViewController.Factory
    private var todoViewController: TodoViewController? = null

    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory
    private val todoViewModel: TodoViewModel by viewModels {
        todoViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        todoFragmentComponent =
            (requireContext().applicationContext as TodoApplication).appComponent.todoFragmentComponent()
                .create()
        todoFragmentComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewController = todoViewControllerFactory.create(this, view, viewLifecycleOwner, todoViewModel)
        todoViewController?.setupViews()
        todoViewController?.setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        todoViewController = null
    }
}