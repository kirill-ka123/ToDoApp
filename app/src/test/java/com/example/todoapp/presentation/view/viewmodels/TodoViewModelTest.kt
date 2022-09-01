package com.example.todoapp.presentation.view.viewmodels

import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.domain.usecases.GetTodoItemsUseCase
import com.example.todoapp.domain.usecases.UpdateTodoItemUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset

class TodoViewModelTest {
    private val testTodoItems = mutableListOf<TodoItem>()
    private val getTodoItemsUseCase: GetTodoItemsUseCase = mock()
    private val updateTodoItemUseCase: UpdateTodoItemUseCase = mock()

    @Before
    fun setUp() {
        ('a'..'z').forEachIndexed { index, c ->
            val createdAt = (1L..100L).random()
            testTodoItems.add(
                TodoItem(
                    id = index.toString(),
                    text = c.toString(),
                    importance = Importance.IMPORTANT,
                    done = index % 2 == 1,
                    createdAt = createdAt,
                    changedAt = createdAt + 1
                )
            )
        }

        testTodoItems.shuffle()
    }

    @After
    fun tearDown() {
        reset(getTodoItemsUseCase)
        reset(updateTodoItemUseCase)
    }

    @Test
    fun `should sort todoItems by creation time and return them`() {
        val todoViewModel = TodoViewModel(getTodoItemsUseCase, updateTodoItemUseCase)

        val todoItems = todoViewModel.sortTodoItems(testTodoItems)

        for (i in 0 until todoItems.size - 1) {
            assert(todoItems[i].createdAt!! <= todoItems[i + 1].createdAt!!)
        }
    }
}