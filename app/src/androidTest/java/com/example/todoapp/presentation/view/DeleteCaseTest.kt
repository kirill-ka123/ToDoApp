package com.example.todoapp.presentation.view

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.todoapp.R
import junit.framework.Assert.assertEquals
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DeleteCaseTest {
    @Rule
    @JvmField
    var mActivityTestRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var webServer: MockWebServer

    @Before
    fun setUp() {
        webServer = MockWebServer()
        webServer.start(8080)
    }

    @After
    fun tearDown() {
        webServer.shutdown()
    }

    @Test
    fun test_deleteCase() {
        var request: RecordedRequest

        lateinit var recyclerView: RecyclerView
        mActivityTestRule.scenario.onActivity {
            recyclerView = it.findViewById(R.id.rvCases)
        }
        val todoAdapter = recyclerView.adapter!! as TodoAdapter
        val itemCount = todoAdapter.differ.currentList.size
        if (itemCount == 0) return
        val elementPosition = 5
        val elementId = todoAdapter.differ.currentList[elementPosition].id

        // Проверяем, что при заходе в приложение открылся TodoFragment
        onView(withId(R.id.parentTodoFragment)).check(matches(isDisplayed()))
        // Проверяем, что при заходе в приложение отправился корректный GET запрос на сервер
        request = webServer.takeRequest()
        assertEquals(GET_REQUEST, request.requestLine)
        // Переходим к редактированию задачи с позицией elementPosition
        onView(withId(R.id.rvCases)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(elementPosition),
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                elementPosition,
                click()
            )
        )
        // Проверяем, что открылся CaseFragment
        onView(withId(R.id.parentCaseFragment)).check(matches(isDisplayed()))
        // Удаление задачи и переход на главный экран
        onView(withId(R.id.tvDelete)).perform(click())
        // Проверяем, что при нажатии кнопки удалить отправился корректный DELETE запрос на сервер
        request = webServer.takeRequest()
        if (request.requestLine == GET_REQUEST) {
            request = webServer.takeRequest()
        }
        assertEquals("$DELETE_REQUEST1$elementId$DELETE_REQUEST2", request.requestLine)
        // Проверяем, что после нажатия кнопки удалить, открылся TodoFragment
        onView(withId(R.id.parentTodoFragment)).check(matches(isDisplayed()))
        // Проверяем, что количество элментов в ресайклере уменьшилось на единицу
        assertEquals(todoAdapter.differ.currentList.size, itemCount - 1)
        // Проверяем, что удалился элемент с нужным id
        todoAdapter.differ.currentList.forEach { todoItem ->
            assertThat(todoItem.id, not(equalTo(elementId)))
        }
    }

    companion object {
        const val GET_REQUEST = "GET /list HTTP/1.1"
        const val DELETE_REQUEST1 = "DELETE /list/"
        const val DELETE_REQUEST2 = " HTTP/1.1"
    }
}
