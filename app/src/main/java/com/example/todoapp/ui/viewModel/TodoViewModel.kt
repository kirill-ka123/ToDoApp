package com.example.todoapp.ui.viewModel

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.todoapp.R
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import com.example.todoapp.repository.TodoItemsRepository
import java.text.DateFormat
import java.util.*

class TodoViewModel(private val todoItemsRepository: TodoItemsRepository) : ViewModel() {
    var visibleOrInvisible = "visible"

    fun saveTodoItem(todoItem: TodoItem) = todoItemsRepository.upsertTodoItem(todoItem)

    fun deleteTodoItem(todoItem: TodoItem) = todoItemsRepository.deleteTodoItem(todoItem)

    fun getTodoItemsLiveData() = todoItemsRepository.todoItemsLiveData

    fun getTodoItems() = todoItemsRepository.getTodoItems()

    fun convertStringToImportance(str: String, view: View) =
        when (str) {
            view.resources.getString(R.string.no) -> Importance.BASIC
            view.resources.getString(R.string.low) -> Importance.LOW
            view.resources.getString(R.string.important) -> Importance.IMPORTANT
            else -> {
                throw IllegalArgumentException()
            }
        }

    fun convertImportanceToInt(importance: Importance) =
        when (importance) {
            Importance.BASIC -> 0
            Importance.LOW -> 1
            Importance.IMPORTANT -> 2
        }

    fun convertUnixToDate(time: Long): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time * 1000
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
    }
}