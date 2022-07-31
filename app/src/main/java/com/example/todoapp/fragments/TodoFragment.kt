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
import com.example.todoapp.decor.ItemTouchHelperCallback
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.android.synthetic.main.todo_fragment.*

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

        todoItemsRepository.todoItemsLiveData.observe(viewLifecycleOwner) {
            var numberOfCompleted = 0
            it.forEach { todoItem ->
                if (todoItem.done) {
                    numberOfCompleted++
                }
            }
            complete_title.text =
                getString(R.string.number_of_completed, numberOfCompleted.toString())
            casesAdapter.differ.submitList(it.toList())
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