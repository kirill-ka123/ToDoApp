package com.example.todoapp.presentation.view.screens

import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.models.StateNetwork
import com.example.todoapp.databinding.TodoFragmentBinding
import com.example.todoapp.presentation.view.FabAnimation
import com.example.todoapp.presentation.view.ItemTouchHelperCallback
import com.example.todoapp.presentation.view.TodoAdapter
import com.example.todoapp.presentation.view.viewmodels.TodoViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TodoViewController @AssistedInject constructor(
    @Assisted("TodoFragment") private val fragment: TodoFragment,
    @Assisted("TodoFragmentView") private val rootView: View,
    @Assisted("TodoFragmentBinding") private val binding: TodoFragmentBinding,
    @Assisted("TodoLifecycleOwner") private val lifecycleOwner: LifecycleOwner,
    @Assisted("TodoViewModel") private val viewModel: TodoViewModel,
    @Assisted("itemTouchHelper") private val itemTouchHelperCallback: ItemTouchHelperCallback?,
    @Assisted("fabAnimation") private val fabAnimation: FabAnimation?,
    private val adapter: TodoAdapter,
    private val connectivityManager: ConnectivityManager,
    private val checkInternet: CheckInternet
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("TodoFragment") fragment: TodoFragment,
            @Assisted("TodoFragmentView") rootView: View,
            @Assisted("TodoFragmentBinding") binding: TodoFragmentBinding,
            @Assisted("TodoLifecycleOwner") lifecycleOwner: LifecycleOwner,
            @Assisted("TodoViewModel") viewModel: TodoViewModel,
            @Assisted("itemTouchHelper") itemTouchHelperCallback: ItemTouchHelperCallback?,
            @Assisted("fabAnimation") fabAnimation: FabAnimation?
        ): TodoViewController
    }

    fun setupViews() {
        setupRecyclerView()
        setupVisibility()
        setupVisibilityClickListener()
        setupFabClickListener()
        setupInternetTitle()
    }

    private fun setupInternetTitle() {
        if (!checkInternet.hasInternetConnection()) {
            binding.internetTitle.visibility = View.VISIBLE
        }
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
                adapter.differ.submitList(getNotCompletedTodoItems(viewModel.todoItems))
            } else {
                binding.ivVisibility.setImageResource(R.drawable.ic_visibility_off)
                viewModel.stateVisibility = StateVisibility.VISIBLE
                adapter.differ.submitList(viewModel.todoItems)
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
            val newTodoItem =
                todoItem.copy(done = isChecked, changedAt = System.currentTimeMillis() / 1000L)
            viewModel.editTodoItem(newTodoItem)
        }
    }

    private fun setupItemTouchHelper(todoAdapter: TodoAdapter) {
        itemTouchHelperCallback?.let {
            ItemTouchHelper(it).attachToRecyclerView(binding.rvCases)
        }
    }

    fun setupTodoItemsObserver() {
        viewModel.getTodoItemsLiveData().observe(lifecycleOwner) { todoItems ->
            val sortedTodoItems = viewModel.sortTodoItems(todoItems)
            viewModel.todoItems = sortedTodoItems

            if (sortedTodoItems.isEmpty()) {
                fabAnimation?.startAnimation()
            } else fabAnimation?.endAnimation()

            binding.completeTitle.text =
                fragment.getString(
                    R.string.number_of_completed,
                    getNumberOfCompleted(sortedTodoItems)
                )

            if (viewModel.stateVisibility == StateVisibility.VISIBLE) {
                adapter.differ.submitList(sortedTodoItems)
            } else {
                adapter.differ.submitList(getNotCompletedTodoItems(sortedTodoItems))
            }

            val recyclerView = fragment.requireActivity().findViewById<RecyclerView>(R.id.rvCases)
            val nested =
                fragment.requireActivity().findViewById<NestedScrollView>(R.id.nestedScrollView)
            nested.fullScroll(ScrollView.FOCUS_DOWN)
            recyclerView.smoothScrollToPosition(15)
//            val layoutManager = recyclerView.layoutManager!!
//            recyclerView.post {
//                layoutManager.smoothScrollToPosition(recyclerView, 15)
//            }
            //recyclerView.scrollToPosition(13)
        }
    }

    fun setupNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(viewModel.networkCallback)
        setupStateNetworkObserver()
    }

    fun unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(viewModel.networkCallback)
        } catch (e: Exception) {
            Log.e("network", "NetworkCallback was not registered or already unregistered")
        }
    }

    private fun setupStateNetworkObserver() {
        viewModel.stateNetwork.observe(lifecycleOwner) { stateNetwork ->
            when (stateNetwork) {
                StateNetwork.AVAILABLE -> binding.internetTitle.visibility = View.GONE
                StateNetwork.LOST -> binding.internetTitle.visibility = View.VISIBLE
            }
        }
    }

    private fun getNumberOfCompleted(todoItems: List<TodoItem>): String {
        var numberOfCompleted = 0
        todoItems.forEach { todoItem ->
            if (todoItem.done == true) {
                numberOfCompleted++
            }
        }
        return numberOfCompleted.toString()
    }

    private fun getNotCompletedTodoItems(todoItems: List<TodoItem>): List<TodoItem> {
        val newList = mutableListOf<TodoItem>()
        todoItems.forEach { todoItem ->
            if (todoItem.done == false) {
                newList.add(todoItem)
            }
        }
        return newList.toList()
    }
}