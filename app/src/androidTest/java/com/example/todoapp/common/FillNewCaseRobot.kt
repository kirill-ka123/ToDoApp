package com.example.todoapp.common

import com.example.todoapp.R
import com.example.todoapp.data.db.models.Importance

class FillNewCaseRobot : BaseTestRobot() {
    fun setCaseText(text: String) = fillEditText(R.id.etCase, text)

    fun setCaseImportance(importance: Importance) = fillSpinner(R.id.spinnerImportance, importance)

    fun setCaseDeadLine() = fillDeadlineInMaterialDatePicker(R.id.switchDeadline)
}