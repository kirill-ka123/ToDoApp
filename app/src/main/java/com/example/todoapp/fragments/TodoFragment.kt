package com.example.todoapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.android.synthetic.main.todo_fragment.*

class TodoFragment : Fragment(R.layout.todo_fragment) {
    lateinit var todoItemsRepository: TodoItemsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoItemsRepository = (activity as MainActivity).todoItemsRepository

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_caseFragment)
        }
    }
}