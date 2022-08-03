package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.common.Utils
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import com.example.todoapp.ui.viewModels.caseViewModel.CaseViewModel
import com.example.todoapp.ui.viewModels.caseViewModel.CaseViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.case_fragment.*

class CaseFragment : Fragment(R.layout.case_fragment) {
    private val caseViewModel: CaseViewModel by viewModels {
        CaseViewModelFactory(TodoItemsRepository.getRepository())
    }
    private val args: CaseFragmentArgs by navArgs()
    private var deadline: Long = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todoItem = args.case
        initViews(todoItem)

        ivClose.setOnClickListener {
            findNavController().popBackStack()
            //findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
        }

        delete.setOnClickListener {
            if (todoItem != null) {
                caseViewModel.deleteTodoItem(todoItem)
            }
            findNavController().popBackStack()
            //findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
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
            if (todoItem == null) {
                if (etCase.text.toString() != "") {
                    val newTodoItem = createNewTodoItem(importance)
                    caseViewModel.saveTodoItem(newTodoItem)
                    findNavController().popBackStack()
                    //findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_empty_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val changedTodoItem = changeTodoItem(todoItem, importance)
                caseViewModel.saveTodoItem(changedTodoItem)
                findNavController().popBackStack()
                //findNavController().navigate(R.id.action_caseFragment_to_todoFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //todoViewModel = null
        deadline = 0L
    }

    private fun createNewTodoItem(importance: Importance) = TodoItem(
        "",
        etCase.text.toString(),
        importance,
        deadline,
        false,
        System.currentTimeMillis() / 1000L
    )

    private fun changeTodoItem(todoItem: TodoItem, importance: Importance) = todoItem.copy(
        text = etCase.text.toString(),
        importance = importance,
        deadline = deadline,
        changedAt = System.currentTimeMillis() / 1000L
    )

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
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_layout,
            R.id.tvSpinner,
            listOf(getString(R.string.no), getString(R.string.low), getString(R.string.important))
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