package com.example.todoapp.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import kotlinx.android.synthetic.main.case_fragment.*

class CaseFragment: Fragment(R.layout.case_fragment) {
    lateinit var todoItemsRepository: TodoItemsRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoItemsRepository = (activity as MainActivity).todoItemsRepository

        iv_close.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        iv_delete.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        tv_delete.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        tv_save.setOnClickListener {
            val importance = when (spinner_importance.selectedItem.toString()) {
                "Нет" -> Importance.BASIC
                "Низкий" -> Importance.LOW
                "Высокий" -> Importance.IMPORTANT
                else -> {
                    throw IllegalArgumentException()
                }
            }
            val todoItem = TodoItem(
                ((todoItemsRepository.getNumberOfTodoItems() ?: 0) + 1).toString(),
                et_case.text.toString(),
                importance,
                1,
                switch_deadline.isChecked,
                1,
                1
            )
            todoItemsRepository.addNewTodoItem(todoItem)
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }
    }
}