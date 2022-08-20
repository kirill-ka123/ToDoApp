package com.example.todoapp.view.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import com.example.todoapp.TodoApplication
import com.example.todoapp.databinding.TodoFragmentBinding
import com.example.todoapp.di.TodoFragmentComponent
import com.example.todoapp.view.CustomSnackbar
import com.example.todoapp.view.FabAnimation
import com.example.todoapp.view.ItemTouchHelperCallback
import com.example.todoapp.view.getColorFromAttr
import com.example.todoapp.view.viewmodels.TodoViewModel
import com.example.todoapp.view.viewmodels.TodoViewModelFactory
import javax.inject.Inject

class TodoFragment : Fragment(R.layout.todo_fragment) {
    private lateinit var todoFragmentComponent: TodoFragmentComponent

    @Inject
    lateinit var todoViewControllerFactory: TodoViewController.Factory
    private var todoViewController: TodoViewController? = null

    @Inject
    lateinit var itemTouchHelperCallbackFactory: ItemTouchHelperCallback.Factory
    private var itemTouchHelperCallback: ItemTouchHelperCallback? = null

    @Inject
    lateinit var todoViewModelFactory: TodoViewModelFactory
    private val todoViewModel: TodoViewModel by viewModels {
        todoViewModelFactory
    }

    private var _binding: TodoFragmentBinding? = null
    private val binding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        todoFragmentComponent =
            (requireContext().applicationContext as TodoApplication).appComponent.todoFragmentComponent()
                .create()
        todoFragmentComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TodoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemTouchHelperCallback = itemTouchHelperCallbackFactory.create(
            todoViewModel,
            view,
            CustomSnackbar(
                layoutInflater.inflate(
                    R.layout.custom_snackbar,
                    LinearLayout(requireContext()),
                    false
                ),
                5000
            )
        )
        todoViewController =
            todoViewControllerFactory.create(
                this,
                view,
                binding,
                viewLifecycleOwner,
                todoViewModel,
                itemTouchHelperCallback,
                FabAnimation(
                    binding.fab,
                    requireContext().getColorFromAttr(com.google.android.material.R.attr.colorSecondary),
                    requireContext().getColorFromAttr(com.google.android.material.R.attr.colorSecondaryVariant)
                )
            )
        todoViewController?.apply {
            setupViews()
            setupTodoItemsObserver()
            setupNetworkCallback()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        todoViewController?.unregisterNetworkCallback()
        todoViewController?.endAnimation()
        todoViewController = null
        itemTouchHelperCallback = null
        _binding = null
    }
}