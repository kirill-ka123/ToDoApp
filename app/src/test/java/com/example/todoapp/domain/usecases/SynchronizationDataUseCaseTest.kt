package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.repository.TodoItemsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SynchronizationDataUseCaseTest {
    private lateinit var testTodoItems: MutableList<TodoItem>

    private lateinit var synchronizationDataUseCase: SynchronizationDataUseCase
    private val todoItemsRepository: TodoItemsRepository = mock()
    private val sessionManager: SessionManager = mock()
    private val synchronizationNetworkUseCase: SynchronizationNetworkUseCase = mock()
    private val synchronizationDatabaseUseCase: SynchronizationDatabaseUseCase = mock()

    @Before
    fun setUp() {
        testTodoItems = mutableListOf()
        ('a'..'z').forEachIndexed { index, c ->
            testTodoItems.add(
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
        testTodoItems.shuffle()

        synchronizationDataUseCase = SynchronizationDataUseCase(
            todoItemsRepository,
            sessionManager,
            synchronizationNetworkUseCase,
            synchronizationDatabaseUseCase
        )
    }

    @After
    fun tearDown() {
        reset(todoItemsRepository)
        reset(sessionManager)
        reset(synchronizationNetworkUseCase)
        reset(synchronizationDatabaseUseCase)
    }

    @Test
    fun `should get revisions, compare them and synchronize database`() {
        val testDatabaseRevision = 10
        val testNetworkRevision = 20

        runBlocking {
            whenever(sessionManager.fetchRevisionNetwork()).thenReturn(testNetworkRevision)
            whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testDatabaseRevision)

            synchronizationDataUseCase.synchronizeData(testTodoItems)

            // Проверяем, что вызываются методы для четния ревизий
            verify(sessionManager).fetchRevisionNetwork()
            verify(sessionManager).fetchRevisionDatabase()

            // Проверяем, что после сравнения ревизий, вызывается метод синхронизации базы данных
            verify(synchronizationDatabaseUseCase).synchronizeDatabase(anyOrNull(), anyOrNull())
            verify(synchronizationNetworkUseCase, never()).synchronizeNetwork(anyOrNull())
        }
    }

    @Test
    fun `should get revisions, compare them and synchronize network`() {
        val testDatabaseRevision = 20
        val testNetworkRevision = 10

        runBlocking {
            whenever(sessionManager.fetchRevisionNetwork()).thenReturn(testNetworkRevision)
            whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testDatabaseRevision)

            synchronizationDataUseCase.synchronizeData(testTodoItems)

            // Проверяем, что вызываются методы для четния ревизий
            verify(sessionManager).fetchRevisionNetwork()
            verify(sessionManager).fetchRevisionDatabase()

            // Проверяем, что после сравнения ревизий, вызывается метод синхронизации сети
            verify(synchronizationDatabaseUseCase, never()).synchronizeDatabase(
                anyOrNull(),
                anyOrNull()
            )
            verify(synchronizationNetworkUseCase).synchronizeNetwork(anyOrNull())
        }
    }
}