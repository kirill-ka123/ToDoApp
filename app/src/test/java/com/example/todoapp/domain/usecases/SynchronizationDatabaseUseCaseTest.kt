package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SynchronizationDatabaseUseCaseTest {
    private lateinit var testTodoItemsFromNetwork: MutableList<TodoItem>
    private lateinit var testTodoItemsFromDatabase: MutableList<TodoItem>

    private lateinit var synchronizationDatabaseUseCase: SynchronizationDatabaseUseCase
    private val todoItemsDao: TodoItemsDao = mock()
    private val sessionManager: SessionManager = mock()

    @Before
    fun setUp() {
        testTodoItemsFromNetwork = mutableListOf()
        ('a'..'z').forEachIndexed { index, c ->
            val createdAt = (1L..100L).random()
            testTodoItemsFromNetwork.add(
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
        testTodoItemsFromNetwork.shuffle()

        testTodoItemsFromDatabase = mutableListOf()
        ('f'..'z').forEachIndexed { index, c ->
            testTodoItemsFromDatabase.add(
                TodoItem(
                    id = index.toString(),
                    text = c.toString(),
                    importance = Importance.IMPORTANT,
                    done = index % 2 == 1,
                    createdAt = index.toLong(),
                    changedAt = (index + 1).toLong()
                )
            )
        }
        testTodoItemsFromDatabase.shuffle()

        synchronizationDatabaseUseCase = SynchronizationDatabaseUseCase(
            todoItemsDao,
            sessionManager
        )
    }

    @After
    fun tearDown() {
        reset(todoItemsDao)
        reset(sessionManager)
    }

    @Test
    fun `should get todoItems from network and synchronize data`() {
        val testNetworkRevision = 20

        runBlocking {
            whenever(sessionManager.fetchRevisionNetwork()).thenReturn(testNetworkRevision)

            synchronizationDatabaseUseCase.synchronizeDatabase(
                testTodoItemsFromNetwork,
                testTodoItemsFromDatabase
            )

            // Проверяем, что вызывается запрос очистки данных из базы данных
            verify(todoItemsDao).deleteAllTodoItems()

            // Проверяем, что объединенные данные из сети и базы данных добавляются в базу данных
            verify(todoItemsDao).addAllTodoItems(
                mergeData(
                    testTodoItemsFromNetwork,
                    testTodoItemsFromDatabase
                )
            )

            // Проверяем, что после добавления в базу данных актуальных данных, просзодит снхронизация ревизий
            verify(sessionManager).fetchRevisionNetwork()
            verify(sessionManager).saveRevisionDatabase(testNetworkRevision)
        }
    }

    private fun mergeData(
        todoItemsFromNetwork: List<TodoItem>,
        todoItemsFromDatabase: List<TodoItem>
    ): List<TodoItem> {
        val newList = mutableListOf<TodoItem>()
        todoItemsFromNetwork.forEach { todoItemFromNetwork ->
            val todoItem = todoItemsFromDatabase.find { todoItemFromDatabase ->
                todoItemFromNetwork.id == todoItemFromDatabase.id
            }
            if (todoItem == null) {
                newList.add(todoItemFromNetwork)
            } else if (todoItem.changedAt ?: 0 > todoItemFromNetwork.changedAt ?: 0) {
                newList.add(todoItem)
            } else newList.add(todoItemFromNetwork)
        }
        return newList
    }
}