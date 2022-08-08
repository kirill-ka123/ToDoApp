package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.common.StateRequest
import com.example.todoapp.common.StateVisibility
import com.example.todoapp.decor.ItemTouchHelperCallback
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import com.example.todoapp.ui.adapters.CasesAdapter
import com.example.todoapp.ui.viewModels.todoViewModel.TodoViewModel
import com.example.todoapp.ui.viewModels.todoViewModel.TodoViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.todo_fragment.*

class TodoFragment : Fragment(R.layout.todo_fragment) {
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(requireActivity().application, TodoItemsRepository)
    }
    private var casesAdapter: CasesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setupErrorObservers(view)

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_caseFragment)
        }

        if (todoViewModel.stateVisibility == StateVisibility.VISIBLE) {
            ivVisibility.setImageResource(R.drawable.ic_visibility_off)
        } else {
            ivVisibility.setImageResource(R.drawable.ic_visibility)
        }

        ivVisibility.setOnClickListener {
            if (todoViewModel.stateVisibility == StateVisibility.VISIBLE) {
                ivVisibility.setImageResource(R.drawable.ic_visibility)
                todoViewModel.stateVisibility = StateVisibility.INVISIBLE
                casesAdapter?.differ?.submitList(getNotCompletedTodoItems(todoViewModel.getTodoItems()))
            } else {
                ivVisibility.setImageResource(R.drawable.ic_visibility_off)
                todoViewModel.stateVisibility = StateVisibility.VISIBLE
                casesAdapter?.differ?.submitList(todoViewModel.getTodoItems())
            }
        }

        todoViewModel.getTodoItemsLive().observe(viewLifecycleOwner) { todoItems ->
            completeTitle.text =
                getString(R.string.number_of_completed, getNumberOfCompleted(todoItems))

            if (todoViewModel.stateVisibility == StateVisibility.VISIBLE) {
                casesAdapter?.differ?.submitList(todoItems)
            } else {
                casesAdapter?.differ?.submitList(getNotCompletedTodoItems(todoItems))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        casesAdapter = null
    }

    private fun setupRecyclerView(view: View) {
        casesAdapter = CasesAdapter()
        casesAdapter?.let { casesAdapter ->
            casesAdapter.setOnItemClickListener { todoItem ->
                val bundle = bundleOf("case" to todoItem)
                findNavController().navigate(R.id.action_todoFragment_to_caseFragment, bundle)
            }
            casesAdapter.setOnCheckboxClickListener { todoItem, isChecked ->
                val newTodoItem = todoItem.copy(done = isChecked)
                todoViewModel.putTodoItemNetwork(newTodoItem)
            }
            val itemTouchHelperCallback =
                ItemTouchHelperCallback(todoViewModel, casesAdapter, view)
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvCases)
        }
        rvCases.apply {
            adapter = casesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setupErrorObservers(view: View) {
        // Для get запроса
        todoViewModel.getStateGetRequestLive().observe(viewLifecycleOwner) { state ->
            when (state) {
                is StateRequest.Error -> {
                    tv_error.text = getString(state.message ?: R.string.something_went_wrong)
                    btn_error.setOnClickListener {
                        todoViewModel.getTodoItemsNetwork()
                    }
                    error.visibility = View.VISIBLE
                }
                is StateRequest.Success -> {
                    error.visibility = View.GONE
                }
            }
        }

        // Для post, put, delete запросов
        todoViewModel.getStateSetRequestLive().observe(viewLifecycleOwner) { state ->
            when (state) {
                is StateRequest.Error -> {
                    state.message?.let {
                        Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                    }
                }
                is StateRequest.Success -> {}
            }
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