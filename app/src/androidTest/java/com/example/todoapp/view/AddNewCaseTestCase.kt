package com.example.todoapp.view

import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.todoapp.R
import org.hamcrest.Matchers
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
        //TransportInteractions.onCheckForImportance().perform(click())
        //TransportInteractions.onSwitchForDeadline().perform(click())
        TransportInteractions.onSaveButton().perform(click())

        Matchers.allOf(
            withId(R.id.tvSave),
            ViewMatchers.withText("SAVE")
        )

//        val materialButton = onView(
//            allOf(
//                withId(androidx.appcompat.R.id.confirm_button), withText("OK"),
//                childAtPosition(
//                    allOf(
//                        withId(androidx.appcompat.R.id.date_picker_actions),
//                        childAtPosition(
//                            withId(androidx.appcompat.R.id.mtrl_calendar_main_pane),
//                            1
//                        )
//                    ),
//                    1
//                ),
//                isDisplayed()
//            )
//        )
//        materialButton.perform(click())

    }
}
