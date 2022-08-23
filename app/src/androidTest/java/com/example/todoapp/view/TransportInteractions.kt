package com.example.todoapp.view

import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.todoapp.R
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf

object TransportInteractions {
    fun onFabButton(): ViewInteraction = onView(withId(R.id.fab))

    fun onEditTextForCase(): ViewInteraction = onView(withId(R.id.etCase))

    fun onCheckForImportance(): DataInteraction {
        onView(withId(R.id.spinnerImportance)).perform(click())
        return Espresso.onData(Matchers.anything())
    }

    fun onSwitchForDeadline(): ViewInteraction = onView(
        allOf(
            withId(R.id.switchDeadline),
            ViewMatchers.withText("Do it before")
        )
    )

    fun onSaveButton(): ViewInteraction = onView(
        allOf(
            withId(R.id.tvSave),
            ViewMatchers.withText("SAVE")
        )
    )
}

