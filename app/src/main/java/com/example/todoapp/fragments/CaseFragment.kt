package com.example.todoapp.fragments

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.case_fragment.*
import java.util.*

class CaseFragment : Fragment(R.layout.case_fragment) {
    private lateinit var todoItemsRepository: TodoItemsRepository
    private val args: CaseFragmentArgs by navArgs()
    private var deadline: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoItemsRepository = (activity as MainActivity).todoItemsRepository
        val todoItem = args.case

        todoItem?.let {
            et_case.setText(it.text)
            spinner_importance.setSelection(convertImportanceToInt(it.importance))
            if (it.deadline != null) {
                switch_deadline.isChecked = true
                tv_date.visibility = View.VISIBLE
                tv_date.text = convertUnixToDate(it.deadline)
            }
        }

        iv_close.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        iv_delete.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        tv_delete.setOnClickListener {
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
            val importance = convertStringToImportance(spinner_importance.selectedItem.toString())
            val newTodoItem: TodoItem
            if (todoItem == null) {
                newTodoItem = TodoItem(
                    ((todoItemsRepository.getNumberOfTodoItems() ?: 0) + 1).toString(),
                    et_case.text.toString(),
                    importance,
                    deadline,
                    false,
                    System.currentTimeMillis() / 1000L
                )
            } else {
                newTodoItem = TodoItem(
                    todoItem.id,
                    et_case.text.toString(),
                    importance,
                    deadline,
                    false,
                    todoItem.created_at,
                    System.currentTimeMillis() / 1000L
                )
            }
            todoItemsRepository.upsertTodoItem(newTodoItem)
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
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

        datePicker.addOnPositiveButtonClickListener {
            tv_date.apply {
                deadline = it / 1000L
                visibility = View.VISIBLE
                text = datePicker.headerText
            }
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun convertStringToImportance(str: String) =
        when (str) {
            "Нет" -> Importance.BASIC
            "Низкий" -> Importance.LOW
            "Высокий" -> Importance.IMPORTANT
            else -> {
                throw IllegalArgumentException()
            }
        }

    private fun convertImportanceToInt(importance: Importance) =
        when (importance) {
            Importance.BASIC -> 0
            Importance.LOW -> 1
            Importance.IMPORTANT -> 2
        }

    private fun convertUnixToDate(time: Long): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time * 1000
        return DateFormat.format("dd-MM-yyyy", calendar).toString()
    }
}