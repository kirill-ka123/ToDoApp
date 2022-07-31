package com.example.todoapp.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.adapters.CasesAdapter
import com.example.todoapp.data.Visibility
import com.example.todoapp.decor.ItemTouchHelperCallback
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.android.synthetic.main.todo_fragment.*
import kotlinx.android.synthetic.main.todo_fragment.view.*

class TodoFragment : Fragment(R.layout.todo_fragment) {
    private lateinit var todoItemsRepository: TodoItemsRepository
    private lateinit var casesAdapter: CasesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoItemsRepository = (activity as MainActivity).todoItemsRepository
        setupRecyclerView()

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_caseFragment)
        }

        iv_visibility.setOnClickListener {
            if (Visibility.visibleOfInvisible == "visible") {
                val newList = mutableListOf<TodoItem>()
                casesAdapter.differ.currentList.forEach { todoItem ->
                    if (!todoItem.done) {
                        newList.add(todoItem)
                    }
                }
                casesAdapter.differ.submitList(newList.toList())

                iv_visibility.setImageResource(R.drawable.ic_visibility)
                Visibility.visibleOfInvisible = "invisible"
            } else {
                casesAdapter.differ.submitList(todoItemsRepository.getTodoItems())

                iv_visibility.setImageResource(R.drawable.ic_visibility_off)
                Visibility.visibleOfInvisible = "visible"
            }
        }

        todoItemsRepository.todoItemsLiveData.observe(viewLifecycleOwner) { todoItems ->
            var numberOfCompleted = 0
            todoItems.forEach { todoItem ->
                if (todoItem.done) {
                    numberOfCompleted++
                }
            }
            complete_title.text =
                getString(R.string.number_of_completed, numberOfCompleted.toString())

            if (Visibility.visibleOfInvisible == "visible") {
                casesAdapter.differ.submitList(todoItems.toList())
            } else {
                val newList = mutableListOf<TodoItem>()
                todoItems.forEach { todoItem ->
                    if (!todoItem.done) {
                        newList.add(todoItem)
                    }
                }
                casesAdapter.differ.submitList(newList.toList())
            }
        }

        val itemTouchHelperCallback =
            ItemTouchHelperCallback(todoItemsRepository, casesAdapter, view)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_cases)
    }

    private fun setupRecyclerView() {
        casesAdapter = CasesAdapter()
        casesAdapter.setOnItemClickListener { todoItem ->
            val bundle = bundleOf("case" to todoItem)
            findNavController().navigate(R.id.action_todoFragment_to_caseFragment, bundle)
        }
        casesAdapter.setOnCheckboxClickListener { todoItem, isChecked ->
            val newTodoItem = todoItem.copy(done = isChecked)
            todoItemsRepository.upsertTodoItem(newTodoItem)
        }
        rv_cases.apply {
            adapter = casesAdapter
            layoutManager = LinearLayoutManager(activity)
        }

    }
}