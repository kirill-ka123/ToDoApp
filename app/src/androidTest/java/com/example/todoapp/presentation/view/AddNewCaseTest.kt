package com.example.todoapp.presentation.view

import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.todoapp.R
import com.example.todoapp.common.FillNewCaseRobot
import com.example.todoapp.data.db.models.Importance
import com.google.android.material.appbar.AppBarLayout
import junit.framework.Assert.assertEquals
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddNewCaseTest {
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
    fun test_addNewCase() {
        var request: RecordedRequest

        lateinit var recyclerView: RecyclerView
        lateinit var nestedScrollView: NestedScrollView
        mActivityTestRule.scenario.onActivity {
            recyclerView = it.findViewById(R.id.rvCases)
            nestedScrollView = it.findViewById(R.id.nestedScrollView)
            it.findViewById<AppBarLayout>(R.id.appbar).setExpanded(false)
        }
        val todoAdapter = recyclerView.adapter!! as TodoAdapter
        val itemCount = todoAdapter.differ.currentList.size
        val lastElementId = todoAdapter.differ.currentList.lastOrNull()?.id ?: 0

        // Проверяем, что при заходе в приложение открылся TodoFragment
        onView(withId(R.id.parentTodoFragment)).check(matches(isDisplayed()))
        // Проверяем, что при заходе в приложение отправился корректный GET запрос на сервер
        request = webServer.takeRequest()
        assertEquals(GET_REQUEST, request.requestLine)
        // Переход на экран добавелния задачи
        onView(withId(R.id.fab)).perform(click())
        // Проверяем, что открылся CaseFragment
        onView(withId(R.id.parentCaseFragment)).check(matches(isDisplayed()))
        // Заполняем все поля задачи
        fillNewCase {
            setCaseText("Test case")
            setCaseImportance(Importance.IMPORTANT)
            setCaseDeadLine()
        }
        // Сохранение задачи и переход на главный экран
        onView(withId(R.id.tvSave)).perform(click())
        // Проверяем, что при нажатии кнопки сохранить отправился корректный POST запрос на сервер
        request = webServer.takeRequest()
        if (request.requestLine == GET_REQUEST) {
            request = webServer.takeRequest()
        }
        assertEquals(POST_REQUEST, request.requestLine)
        // Проверяем, что после нажатия кнопки сохранить, открылся TodoFragment
        onView(withId(R.id.parentTodoFragment)).check(matches(isDisplayed()))
        // Проверяем, что количество элментов в ресайклере увеличилось на единицу
        assertEquals(todoAdapter.differ.currentList.size, itemCount + 1)
        // Проверяем, что последний элемент ресайклера после добавления новой задачи, отличается
        // от последнего элемента ресаклера до добавления новой задачи
        // То есть проверка на то, что новый элемент добавился именно в конец
        assertThat(todoAdapter.differ.currentList.last().id, not(equalTo(lastElementId)))
        // Скролим ресайклер до последнего элемента
        nestedScrollView.post { nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        // Проверяем, что новая задача отображается в ресайклере
        onView(withId(R.id.rvCases)).check(
            matches(
                withViewAtPosition(
                    itemCount, allOf(
                        withId(R.id.todoItemLayout), isDisplayed()
                    )
                )
            )
        )
    }

    private fun withViewAtPosition(
        position: Int,
        itemMatcher: Matcher<View?>
    ): Matcher<View?> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder =
                    recyclerView.findViewHolderForAdapterPosition(position)
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    private fun fillNewCase(func: FillNewCaseRobot.() -> Unit) = FillNewCaseRobot().apply { func() }

    companion object {
        const val GET_REQUEST = "GET /list HTTP/1.1"
        const val POST_REQUEST = "POST /list HTTP/1.1"
    }
}
