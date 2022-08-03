package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.common.Utils
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.example.todoapp.ui.MainActivity
import com.example.todoapp.ui.viewModel.TodoViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.case_fragment.*

class CaseFragment : Fragment(R.layout.case_fragment) {
    private var todoViewModel: TodoViewModel? = null
    private val args: CaseFragmentArgs by navArgs()
    private var deadline: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel = (activity as MainActivity).todoViewModel

        val todoItem = args.case
        initViews(todoItem)

        ivClose.setOnClickListener {
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        delete.setOnClickListener {
            if (todoItem != null) {
                todoViewModel?.deleteTodoItem(todoItem)
            }
            findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        switchDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePickerDialog()
            } else {
                deadline = 0L
                tvDate.visibility = View.INVISIBLE
            }
        }

        tvSave.setOnClickListener {
            val importance =
                Utils.convertStringIdToImportance(spinnerImportance.selectedItemPosition)
            val newTodoItem: TodoItem
            if (todoItem == null) {
                if (etCase.text.toString() != "") {
                    newTodoItem = TodoItem(
                        todoViewModel?.getTodoItems()?.size.toString(),
                        etCase.text.toString(),
                        importance,
                        deadline,
                        false,
                        System.currentTimeMillis() / 1000L
                    )
                    todoViewModel?.saveTodoItem(newTodoItem)
                    findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_empty_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                newTodoItem = todoItem.copy(
                    text = etCase.text.toString(),
                    importance = importance,
                    deadline = deadline,
                    changedAt = System.currentTimeMillis() / 1000L
                )
                todoViewModel?.saveTodoItem(newTodoItem)
                findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        todoViewModel = null
        deadline = 0L
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
            switchDeadline.isChecked = false
            deadline = 0L
        }

        datePicker.addOnCancelListener {
            switchDeadline.isChecked = false
            deadline = 0L
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            tvDate.apply {
                deadline = selection / 1000L
                visibility = View.VISIBLE
                text = Utils.convertUnixToDate(selection / 1000L)
            }
        }

        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun convertImportanceToInt(importance: Importance) =
        when (importance) {
            Importance.BASIC -> 0
            Importance.LOW -> 1
            Importance.IMPORTANT -> 2
        }


    private fun initViews(todoItem: TodoItem?) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.importance,
            R.layout.spinner_layout
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        spinnerImportance.adapter = adapter

        todoItem?.let {
            etCase.setText(it.text)
            spinnerImportance.setSelection(convertImportanceToInt(it.importance))
            if (it.deadline > 0L) {
                deadline = it.deadline
                switchDeadline.isChecked = true
                tvDate.visibility = View.VISIBLE
                tvDate.text = Utils.convertUnixToDate(it.deadline)
            }
            tvDelete.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            ivDelete.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }
}