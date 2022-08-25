package com.example.todoapp.presentation.view

import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddNewCaseTestCase {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addNewCaseTestCase() {
        TransportInteractions.onFabButton().perform(click())
        TransportInteractions.onEditTextForCase()
            .perform(replaceText("Test case"), closeSoftKeyboard())
        TransportInteractions.onSaveButton().perform(click())
    }
}
