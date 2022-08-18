package com.example.todoapp.view.screens

import android.content.res.Resources
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.data.network.models.StateRequest
import com.example.todoapp.databinding.TodoFragmentBinding
import com.example.todoapp.models.TodoItem
import com.example.todoapp.view.ItemTouchHelperCallback
import com.example.todoapp.view.TodoAdapter
import com.example.todoapp.view.viewmodels.TodoViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.IOException
import java.net.UnknownHostException

class TodoViewController @AssistedInject constructor(
    @Assisted("TodoFragment") private val fragment: TodoFragment,
    @Assisted("TodoFragmentView") private val rootView: View,
    @Assisted("TodoFragmentBinding") private val binding: TodoFragmentBinding,
    @Assisted("TodoLifecycleOwner") private val lifecycleOwner: LifecycleOwner,
    @Assisted("TodoViewModel") private val viewModel: TodoViewModel,
    @Assisted("itemTouchHelper") private val itemTouchHelperCallback: ItemTouchHelperCallback?,
    private val adapter: TodoAdapter
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("TodoFragment") fragment: TodoFragment,
            @Assisted("TodoFragmentView") rootView: View,
            @Assisted("TodoFragmentBinding") binding: TodoFragmentBinding,
            @Assisted("TodoLifecycleOwner") lifecycleOwner: LifecycleOwner,
            @Assisted("TodoViewModel") viewModel: TodoViewModel,
            @Assisted("itemTouchHelper") itemTouchHelperCallback: ItemTouchHelperCallback?
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
            binding.ivVisibility.setImageResource(R.drawable.ic_visibility_off)
        } else {
            binding.ivVisibility.setImageResource(R.drawable.ic_visibility)
        }
    }

    private fun setupFabClickListener() {
        binding.fab.setOnClickListener {
            findNavController(fragment).navigate(R.id.action_todoFragment_to_caseFragment)
        }
    }

    private fun setupVisibilityClickListener() {
        binding.ivVisibility.setOnClickListener {
            if (viewModel.stateVisibility == StateVisibility.VISIBLE) {
                binding.ivVisibility.setImageResource(R.drawable.ic_visibility)
                viewModel.stateVisibility = StateVisibility.INVISIBLE
                adapter.differ.submitList(getNotCompletedTodoItems(viewModel.getTodoItems()))
            } else {
                binding.ivVisibility.setImageResource(R.drawable.ic_visibility_off)
                viewModel.stateVisibility = StateVisibility.VISIBLE
                adapter.differ.submitList(viewModel.getTodoItems())
            }
        }
    }

    private fun setupRecyclerView() {
        setupRecyclerClickListeners(adapter)
        setupItemTouchHelper(adapter)
        binding.rvCases.apply {
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
        itemTouchHelperCallback?.let {
            ItemTouchHelper(it).attachToRecyclerView(binding.rvCases)
        }
    }

    private fun setupTodoItemsObserver() {
        viewModel.getTodoItemsLiveData().observe(lifecycleOwner) { todoItems ->
            binding.completeTitle.text =
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
                    binding.tvError.text = mapError(fragment.resources, state.error)
                    binding.btnError.setOnClickListener {
                        viewModel.getTodoItemsNetwork()
                    }
                    binding.error.visibility = View.VISIBLE
                }
                is StateRequest.Success -> {
                    binding.error.visibility = View.GONE
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

    private fun getNotCompletedTodoItems(todoItems: List<TodoItem>): List<TodoItem> {
        val newList = mutableListOf<TodoItem>()
        todoItems.forEach { todoItem ->
            if (!todoItem.done) {
                newList.add(todoItem)
            }
        }
        return newList.toList()
    }
}