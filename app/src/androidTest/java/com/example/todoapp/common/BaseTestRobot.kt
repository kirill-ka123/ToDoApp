package com.example.todoapp.common

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.todoapp.data.db.models.Importance
import org.hamcrest.Matchers.anything

open class BaseTestRobot {
    fun fillEditText(resId: Int, text: String): ViewInteraction =
        onView(withId(resId)).perform(
            ViewActions.replaceText(text),
            ViewActions.closeSoftKeyboard()
        )

    fun clickButton(resId: Int): ViewInteraction =
        onView((withId(resId))).perform(ViewActions.click())

    fun fillSpinner(spinnerResId: Int, importance: Importance) {
        onView(withId(spinnerResId)).perform(ViewActions.click())
        onData(anything()).atPosition(importance.ordinal).perform(ViewActions.click())
    }

    fun fillDeadlineInMaterialDatePicker(buttonOpenMaterialDatePickerResId: Int) {
        onView(withId(buttonOpenMaterialDatePickerResId)).perform(ViewActions.click())

        // Проверка, что MaterialDatePicker открылся
        onView(withId(com.google.android.material.R.id.confirm_button)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        onView(withId(com.google.android.material.R.id.confirm_button)).perform(ViewActions.click())

        // Проверка, что MaterialDatePicker закрылся
        onView(withId(com.google.android.material.R.id.confirm_button)).check(ViewAssertions.doesNotExist())
    }
}