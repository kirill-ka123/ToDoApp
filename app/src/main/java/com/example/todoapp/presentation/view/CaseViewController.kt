package com.example.todoapp.presentation.view

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.presentation.models.Importance
import com.example.todoapp.presentation.models.TodoItem
import com.example.todoapp.presentation.common.Utils
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.case_fragment.view.*

class CaseViewController(
    private val fragment: CaseFragment,
    private val rootView: View,
    private val viewModel: CaseViewModel,
    args: CaseFragmentArgs
) {
    private var deadline: Long = 0L
    private val todoItem = args.case

    fun setupViews() {
        setupSpinner()
        setupTodoItemInfo()
        setupSwitchClickListener()
        setupCloseClickListener()
        setupDeleteClickListener()
        setupSaveClickListener()
    }

    private fun setupTodoItemInfo() {
        todoItem?.let {
            rootView.etCase.setText(it.text)
            rootView.spinnerImportance.setSelection(convertImportanceToInt(it.importance))
            if (it.deadline > 0L) {
                deadline = it.deadline
                rootView.switchDeadline.isChecked = true
                rootView.tvDate.visibility = View.VISIBLE
                rootView.tvDate.text = Utils.convertUnixToDate(it.deadline)
            }
            rootView.tvDelete.setTextColor(
                ContextCompat.getColor(
                    fragment.requireContext(),
                    R.color.red
                )
            )
            rootView.ivDelete.setColorFilter(
                ContextCompat.getColor(
                    fragment.requireContext(),
                    R.color.red
                )
            )
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            fragment.requireContext(),
            R.layout.spinner_layout,
            R.id.tvSpinner,
            listOf(
                fragment.resources.getString(R.string.no),
                fragment.resources.getString(R.string.low),
                fragment.resources.getString(R.string.important)
            )
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        rootView.spinnerImportance.adapter = adapter
    }

    private fun setupCloseClickListener() {
        rootView.ivClose.setOnClickListener {
            fragment.findNavController().popBackStack()
        }
    }

    private fun setupDeleteClickListener() {
        rootView.delete.setOnClickListener {
            if (todoItem != null) {
                viewModel.deleteTodoItemNetwork(todoItem.id)
            }
            fragment.findNavController().popBackStack()
        }
    }

    private fun setupSwitchClickListener() {
        rootView.switchDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePickerDialog()
            } else {
                deadline = 0L
                rootView.tvDate.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupSaveClickListener() {
        rootView.tvSave.setOnClickListener {
            val importance =
                Utils.convertStringIdToImportance(rootView.spinnerImportance.selectedItemPosition)
            if (todoItem == null) {
                if (rootView.etCase.text.toString() != "") {
                    val newTodoItem = createNewTodoItem(importance)
                    viewModel.postTodoItemNetwork(newTodoItem)
                    fragment.findNavController().popBackStack()
                } else Toast.makeText(
                    fragment.requireContext(),
                    fragment.resources.getString(R.string.toast_empty_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val changedTodoItem = changeTodoItem(todoItem, importance)
                viewModel.putTodoItemNetwork(changedTodoItem)
                fragment.findNavController().popBackStack()
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

        setupDatePickerDialogClickListeners(datePicker)

        datePicker.show(fragment.parentFragmentManager, "datePicker")
    }

    private fun setupDatePickerDialogClickListeners(datePicker: MaterialDatePicker<Long>) {
        datePicker.addOnNegativeButtonClickListener {
            rootView.switchDeadline.isChecked = false
            deadline = 0L
        }

        datePicker.addOnCancelListener {
            rootView.switchDeadline.isChecked = false
            deadline = 0L
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            rootView.tvDate.apply {
                deadline = selection / 1000L
                visibility = View.VISIBLE
                text = Utils.convertUnixToDate(selection / 1000L)
            }
        }
    }

    private fun createNewTodoItem(importance: Importance) = TodoItem(
        "",
        rootView.etCase.text.toString(),
        importance,
        deadline,
        false,
        System.currentTimeMillis() / 1000L
    )

    private fun changeTodoItem(todoItem: TodoItem, importance: Importance) = todoItem.copy(
        text = rootView.etCase.text.toString(),
        importance = importance,
        deadline = deadline,
        changedAt = System.currentTimeMillis() / 1000L
    )

    private fun convertImportanceToInt(importance: Importance) =
        when (importance) {
            Importance.BASIC -> 0
            Importance.LOW -> 1
            Importance.IMPORTANT -> 2
        }
}