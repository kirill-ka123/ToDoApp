package com.example.todoapp.presentation.view.screens

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.common.Utils
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.databinding.CaseFragmentBinding
import com.example.todoapp.presentation.view.getColorFromAttr
import com.example.todoapp.presentation.view.viewmodels.CaseViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class CaseViewController @AssistedInject constructor(
    @Assisted("CaseFragment") private val fragment: CaseFragment,
    @Assisted("CaseFragmentView") private val rootView: View,
    @Assisted("CaseFragmentBinding") private val binding: CaseFragmentBinding,
    @Assisted("CaseViewModel") private val viewModel: CaseViewModel,
    @Assisted("CaseArgs") args: CaseFragmentArgs
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("CaseFragment") fragment: CaseFragment,
            @Assisted("CaseFragmentView") rootView: View,
            @Assisted("CaseFragmentBinding") binding: CaseFragmentBinding,
            @Assisted("CaseViewModel") viewModel: CaseViewModel,
            @Assisted("CaseArgs") args: CaseFragmentArgs
        ): CaseViewController
    }

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
            binding.etCase.setText(it.text)
            binding.spinnerImportance.setSelection(convertImportanceToInt(it.importance))
            if (it.deadline != null && it.deadline > 0L) {
                deadline = it.deadline
                binding.switchDeadline.isChecked = true
                binding.tvDate.visibility = View.VISIBLE
                binding.tvDate.text = Utils.convertUnixToDate(it.deadline)
            }
            binding.tvDelete.setTextColor(
                fragment.requireContext().getColorFromAttr(androidx.appcompat.R.attr.colorError)
            )
            binding.ivDelete.setColorFilter(
                fragment.requireContext().getColorFromAttr(androidx.appcompat.R.attr.colorError)
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
        binding.spinnerImportance.adapter = adapter
    }

    private fun setupCloseClickListener() {
        binding.ivClose.setOnClickListener {
            fragment.findNavController().popBackStack()
        }
    }

    private fun setupDeleteClickListener() {
        binding.delete.setOnClickListener {
            if (todoItem != null) {
                viewModel.deleteTodoItem(todoItem)
            }
            fragment.findNavController().popBackStack()
        }
    }

    private fun setupSwitchClickListener() {
        binding.switchDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePickerDialog()
            } else {
                deadline = 0L
                binding.tvDate.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupSaveClickListener() {
        binding.tvSave.setOnClickListener {
            val importance =
                Utils.convertStringIdToImportance(binding.spinnerImportance.selectedItemPosition)
            if (todoItem == null) {
                if (binding.etCase.text.toString() != "") {
                    val newTodoItem = createNewTodoItem(importance)
                    viewModel.addTodoItem(newTodoItem)
                    fragment.findNavController().popBackStack()
                } else Toast.makeText(
                    fragment.requireContext(),
                    fragment.resources.getString(R.string.toast_empty_text),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val changedTodoItem = changeTodoItem(todoItem, importance)
                viewModel.editTodoItem(changedTodoItem)
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
                setTheme(R.style.Calendar_Blue)
                setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                setCalendarConstraints(calendarConstraints)
                build()
            }

        setupDatePickerDialogClickListeners(datePicker)

        datePicker.show(fragment.parentFragmentManager, "datePicker")
    }

    private fun setupDatePickerDialogClickListeners(datePicker: MaterialDatePicker<Long>) {
        datePicker.addOnNegativeButtonClickListener {
            binding.switchDeadline.isChecked = false
            deadline = 0L
        }

        datePicker.addOnCancelListener {
            binding.switchDeadline.isChecked = false
            deadline = 0L
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            binding.tvDate.apply {
                deadline = selection / 1000L
                visibility = View.VISIBLE
                text = Utils.convertUnixToDate(selection / 1000L)
            }
        }
    }

    private fun createNewTodoItem(importance: Importance) = TodoItem(
        "0",
        binding.etCase.text.toString(),
        importance,
        deadline,
        false,
        System.currentTimeMillis() / 1000L
    )

    private fun changeTodoItem(todoItem: TodoItem, importance: Importance) = todoItem.copy(
        text = binding.etCase.text.toString(),
        importance = importance,
        deadline = deadline,
        changedAt = System.currentTimeMillis() / 1000L
    )

    private fun convertImportanceToInt(importance: Importance?) =
        when (importance) {
            Importance.BASIC -> 0
            Importance.LOW -> 1
            Importance.IMPORTANT -> 2
            else -> 0
        }
}