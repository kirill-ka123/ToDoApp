package com.example.todoapp.repository

import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import java.util.*

class TodoItemsRepository {
    fun getTodoItems() = todoItems.toList()

    fun addNewTodoItem(case: TodoItem) {
        todoItems.add(case)
    }

    companion object {
        private val todoItems = mutableListOf(
            TodoItem(
                UUID.randomUUID(),
                "Сходить к адвокату",
                Importance.BASIC,
                1658950803,
                false,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Написать огромное сочинение по русскому языку на тему: \"Как я классно провел лето и попал в школу мобильной разработки от Яндекса\" продолжение большой строки аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа",
                Importance.LOW,
                1658950803,
                false,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Прогуляться",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "",
                Importance.LOW,
                1658950803,
                false,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            ),
            TodoItem(
                UUID.randomUUID(),
                "Почистить зубы",
                Importance.IMPORTANT,
                1658950803,
                true,
                1658949803,
                1658950203
            )
        )
    }
}