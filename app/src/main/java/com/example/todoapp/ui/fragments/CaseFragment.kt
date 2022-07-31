package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.models.TodoItem
import com.example.todoapp.ui.MainActivity
import com.example.todoapp.ui.viewModel.TodoViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.case_fragment.*

class CaseFragment : Fragment(R.layout.case_fragment) {
    private lateinit var todoViewModel: TodoViewModel
    private val args: CaseFragmentArgs by navArgs()
    private var deadline: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel = (activity as MainActivity).todoViewModel

        val todoItem = args.case
        initViews(todoItem)

        iv_close.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        delete.setOnClickListener {
            if (todoItem != null) {
                todoViewModel.deleteTodoItem(todoItem)
            }
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        switch_deadline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePickerDialog()
            } else {
                tv_date.visibility = View.INVISIBLE
            }
        }

        tv_save.setOnClickListener {
            val importance =
                todoViewModel.convertStringToImportance(spinner_importance.selectedItem.toString(), view)
            val newTodoItem: TodoItem
            if (todoItem == null) {
                if (et_case.text.toString() != "") {
                    newTodoItem = TodoItem(
                        todoViewModel.getTodoItems()?.size.toString(),
                        et_case.text.toString(),
                        importance,
                        deadline,
                        false,
                        System.currentTimeMillis() / 1000L
                    )
                    todoViewModel.saveTodoItem(newTodoItem)
                    findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_empty_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                newTodoItem = todoItem.copy(
                    text = et_case.text.toString(),
                    importance = importance,
                    deadline = deadline,
                    changed_at = System.currentTimeMillis() / 1000L
                )
                todoViewModel.saveTodoItem(newTodoItem)
                findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendarConstraints = CalendarConstraints.Builder().run {
            setValidator(DateValidatorPointForward.now())
            build()
        }

        val datePicker =
            MaterialDatePicker.Builder.datePicker().run {
                setTheme(R.style.colored_calendar)
                setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                setCalendarConstraints(calendarConstraints)
                build()
            }

        datePicker.addOnNegativeButtonClickListener {
            switch_deadline.isChecked = false
            deadline = null
        }

        datePicker.addOnCancelListener {
            switch_deadline.isChecked = false
            deadline = null
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            tv_date.apply {
                deadline = selection / 1000L
                visibility = View.VISIBLE
                text = todoViewModel.convertUnixToDate(selection / 1000L)
            }
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }


    private fun initViews(todoItem: TodoItem?) {
        todoItem?.let {
            et_case.setText(it.text)
            spinner_importance.setSelection(todoViewModel.convertImportanceToInt(it.importance))
            if (it.deadline != null) {
                switch_deadline.isChecked = true
                tv_date.visibility = View.VISIBLE
                tv_date.text = todoViewModel.convertUnixToDate(it.deadline)
            }
            tv_delete.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            iv_delete.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }
}