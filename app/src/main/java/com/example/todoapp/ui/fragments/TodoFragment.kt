package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.decor.ItemTouchHelperCallback
import com.example.todoapp.models.TodoItem
import com.example.todoapp.ui.MainActivity
import com.example.todoapp.ui.adapters.CasesAdapter
import com.example.todoapp.ui.viewModel.TodoViewModel
import kotlinx.android.synthetic.main.todo_fragment.*

class TodoFragment : Fragment(R.layout.todo_fragment) {
    private var todoViewModel: TodoViewModel? = null
    private var casesAdapter: CasesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel = (activity as MainActivity).todoViewModel
        setupRecyclerView()

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_caseFragment)
        }

        ivVisibility.setOnClickListener {
            if (todoViewModel?.visibleOrInvisible == "visible") {
                val newList = mutableListOf<TodoItem>()

                casesAdapter?.let { casesAdapter ->
                    casesAdapter.differ.currentList.forEach { todoItem ->
                        if (!todoItem.done) {
                            newList.add(todoItem)
                        }
                    }
                    casesAdapter.differ.submitList(newList.toList())
                }

                ivVisibility.setImageResource(R.drawable.ic_visibility)
                todoViewModel?.visibleOrInvisible = "invisible"
            } else {
                casesAdapter?.differ?.submitList(todoViewModel?.getTodoItems())

                ivVisibility.setImageResource(R.drawable.ic_visibility_off)
                todoViewModel?.visibleOrInvisible = "visible"
            }

        }


        todoViewModel?.getTodoItemsLiveData()?.observe(viewLifecycleOwner) { todoItems ->
            var numberOfCompleted = 0
            todoItems.forEach { todoItem ->
                if (todoItem.done) {
                    numberOfCompleted++
                }
            }
            completeTitle.text =
                getString(R.string.number_of_completed, numberOfCompleted.toString())

            if (todoViewModel?.visibleOrInvisible == "visible") {
                casesAdapter?.differ?.submitList(todoItems.toList())
            } else {
                val newList = mutableListOf<TodoItem>()
                todoItems.forEach { todoItem ->
                    if (!todoItem.done) {
                        newList.add(todoItem)
                    }
                }
                casesAdapter?.differ?.submitList(newList.toList())
            }
        }
        casesAdapter?.let { casesAdapter ->
            val itemTouchHelperCallback =
                ItemTouchHelperCallback(todoViewModel, casesAdapter, view)
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvCases)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        todoViewModel = null
        casesAdapter = null
    }

    private fun setupRecyclerView() {
        casesAdapter = CasesAdapter()
        casesAdapter?.setOnItemClickListener { todoItem ->
            val bundle = bundleOf("case" to todoItem)
            findNavController().navigate(R.id.action_todoFragment_to_caseFragment, bundle)
        }
        casesAdapter?.setOnCheckboxClickListener { todoItem, isChecked ->
            val newTodoItem = todoItem.copy(done = isChecked)
            todoViewModel?.saveTodoItem(newTodoItem)
        }
        rvCases.apply {
            adapter = casesAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }
}