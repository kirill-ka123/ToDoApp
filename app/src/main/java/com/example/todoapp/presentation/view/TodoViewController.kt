package com.example.todoapp.presentation.view

import android.content.res.Resources
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.presentation.common.StateVisibility
import com.example.todoapp.presentation.data.network.models.StateRequest
import com.example.todoapp.presentation.models.TodoItem
import com.google.android.material.snackbar.Snackbar
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.android.synthetic.main.todo_fragment.view.*
import java.io.IOException
import java.net.UnknownHostException

class TodoViewController @AssistedInject constructor(
    @Assisted("todoFragment") private val fragment: TodoFragment,
    @Assisted("todoFragmentView") private val rootView: View,
    @Assisted("todoLifecycleOwner") private val lifecycleOwner: LifecycleOwner,
    @Assisted("todoViewModel") private val viewModel: TodoViewModel,
    private val adapter: TodoAdapter
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("todoFragment") fragment: TodoFragment,
            @Assisted("todoFragmentView") rootView: View,
            @Assisted("todoLifecycleOwner") lifecycleOwner: LifecycleOwner,
            @Assisted("todoViewModel") viewModel: TodoViewModel,
        ): TodoViewController
    }

    fun setupViews() {
        setupRecyclerView()
        setupVisibility()
        setupVisibilityClickListener()
        setupFabClickListener()
    }

    fun setupObservers() {
        setupTodoItemsObserver()
        setupGetResponseObserver()
        setupSetResponseObserver()
    }

    private fun setupVisibility() {
        if (viewModel.stateVisibility == StateVisibility.VISIBLE) {
            rootView.ivVisibility.setImageResource(R.drawable.ic_visibility_off)
        } else {
            rootView.ivVisibility.setImageResource(R.drawable.ic_visibility)
        }
    }

    private fun setupFabClickListener() {
        rootView.fab.setOnClickListener {
            findNavController(fragment).navigate(R.id.action_todoFragment_to_caseFragment)
        }
    }

    private fun setupVisibilityClickListener() {
        rootView.ivVisibility.setOnClickListener {
            if (viewModel.stateVisibility == StateVisibility.VISIBLE) {
                rootView.ivVisibility.setImageResource(R.drawable.ic_visibility)
                viewModel.stateVisibility = StateVisibility.INVISIBLE
                adapter.differ.submitList(getNotCompletedTodoItems(viewModel.getTodoItems()))
            } else {
                rootView.ivVisibility.setImageResource(R.drawable.ic_visibility_off)
                viewModel.stateVisibility = StateVisibility.VISIBLE
                adapter.differ.submitList(viewModel.getTodoItems())
            }
        }
    }

    private fun setupRecyclerView() {
        setupRecyclerClickListeners(adapter)
        setupItemTouchHelper(adapter)
        rootView.rvCases.apply {
            adapter = this@TodoViewController.adapter
            layoutManager = LinearLayoutManager(fragment.activity)
        }
    }

    private fun setupRecyclerClickListeners(todoAdapter: TodoAdapter) {
        todoAdapter.setOnItemClickListener { todoItem ->
            val bundle = bundleOf("case" to todoItem)
            findNavController(fragment).navigate(
                R.id.action_todoFragment_to_caseFragment,
                bundle
            )
        }
        todoAdapter.setOnCheckboxClickListener { todoItem, isChecked ->
            val newTodoItem = todoItem.copy(done = isChecked)
            viewModel.putTodoItemNetwork(newTodoItem)
        }
    }

    private fun setupItemTouchHelper(todoAdapter: TodoAdapter) {
        val itemTouchHelperCallback =
            ItemTouchHelperCallback(viewModel, todoAdapter, rootView)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rootView.rvCases)
    }

    private fun setupTodoItemsObserver() {
        viewModel.getTodoItemsLiveData().observe(lifecycleOwner) { todoItems ->
            rootView.completeTitle.text =
                fragment.getString(
                    R.string.number_of_completed,
                    getNumberOfCompleted(todoItems)
                )

            if (viewModel.stateVisibility == StateVisibility.VISIBLE) {
                adapter.differ.submitList(todoItems)
            } else {
                adapter.differ.submitList(getNotCompletedTodoItems(todoItems))
            }
        }
    }

    private fun setupGetResponseObserver() {
        viewModel.getStateGetRequestLiveData().observe(lifecycleOwner) { state ->
            when (state) {
                is StateRequest.Error -> {
                    rootView.tv_error.text = mapError(fragment.resources, state.error)
                    rootView.btn_error.setOnClickListener {
                        viewModel.getTodoItemsNetwork()
                    }
                    rootView.error.visibility = View.VISIBLE
                }
                is StateRequest.Success -> {
                    rootView.error.visibility = View.GONE
                }
            }
        }
    }

    private fun setupSetResponseObserver() {
        viewModel.getStateSetRequestLiveData().observe(lifecycleOwner) { state ->
            when (state) {
                is StateRequest.Error -> {
                    Snackbar.make(
                        rootView,
                        mapError(fragment.resources, state.error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                is StateRequest.Success -> {}
            }
        }
    }

    private fun mapError(resources: Resources, t: Throwable?): String {
        return when (t) {
            is UnknownHostException -> resources.getString(R.string.something_went_wrong)
            is IOException -> resources.getString(R.string.no_internet_connection)
            else -> resources.getString(R.string.something_went_wrong)
        }
    }

    private fun getNumberOfCompleted(todoItems: List<TodoItem>): String {
        var numberOfCompleted = 0
        todoItems.forEach { todoItem ->
            if (todoItem.done) {
                numberOfCompleted++
            }
        }
        return numberOfCompleted.toString()
    }

    fun getNotCompletedTodoItems(todoItems: List<TodoItem>): List<TodoItem> {
        val newList = mutableListOf<TodoItem>()
        todoItems.forEach { todoItem ->
            if (!todoItem.done) {
                newList.add(todoItem)
            }
        }
        return newList.toList()
    }
}