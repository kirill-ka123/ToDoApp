package com.example.todoapp.common

import com.example.todoapp.data.db.models.Importance
import java.text.DateFormat
import java.util.*

object Utils {
    fun convertStringIdToImportance(selectedItemPosition: Int) =
        when (selectedItemPosition) {
            0 -> Importance.BASIC
            1 -> Importance.LOW
            2 -> Importance.IMPORTANT
            else -> {
                throw IllegalArgumentException()
            }
        }

    fun convertUnixToDate(time: Long): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time * 1000
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
    }
}