package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.data.repository.TodoItemsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class GetTodoItemsUseCaseTest {
    private lateinit var testTodoItems: MutableList<TodoItem>
    private lateinit var testTodoItem: TodoItem

    private lateinit var getTodoItemsUseCase: GetTodoItemsUseCase
    private val todoItemsDao: TodoItemsDao = mock()
    private val todoApi: TodoApi = mock()
    private val sessionManager: SessionManager = mock()
    private val checkInternet: CheckInternet = mock()
    private val synchronizationDataUseCase: SynchronizationDataUseCase = mock()

    @Before
    fun setUp() {
        testTodoItem = TodoItem(
            id = "0",
            text = "test",
            importance = Importance.IMPORTANT,
            done = false,
            createdAt = 0,
            changedAt = 1
        )

        testTodoItems = mutableListOf()
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

        val todoItemsRepository = TodoItemsRepository(
            todoItemsDao,
            todoApi
        )
        getTodoItemsUseCase = GetTodoItemsUseCase(
            todoItemsRepository,
            sessionManager,
            checkInternet,
            synchronizationDataUseCase
        )
    }

    @After
    fun tearDown() {
        reset(todoItemsDao)
        reset(todoApi)
        reset(sessionManager)
        reset(checkInternet)
    }

    @Test
    fun `should get todoItems from network and synchronize data`() {
        val testNetworkRevision = 20
        val testTodoItemsNetwork = testTodoItems.map { it.mapToTodoItemNetwork() }
        val getItemsResponse =
            GetItemsResponse("ok", testTodoItemsNetwork, testNetworkRevision)

        runBlocking {
            // Проверка без интернета и с интернетом
            for (hasInternetConnection in 0..1) {
                whenever(checkInternet.hasInternetConnection()).thenReturn(hasInternetConnection == 1)
                whenever(todoApi.getTodoItems()).thenReturn(getItemsResponse)
                whenever(sessionManager.fetchRevisionNetwork()).thenReturn(testNetworkRevision)

                getTodoItemsUseCase.getTodoItemsNetwork()

                // Проверяем, что запрос получения дел из облака вызывается в зависимости от наличия интернета
                verify(todoApi, times(hasInternetConnection)).getTodoItems()

                // Проверяем, что после запроса в сеть, вызывается метод сохранения ревизии сети
                verify(sessionManager, times(hasInternetConnection)).saveRevisionNetwork(
                    testNetworkRevision
                )

                // Проверяем, что после получения списка дел из облака вызывается метод синхронизации данных
                verify(synchronizationDataUseCase, times(hasInternetConnection)).synchronizeData(
                    testTodoItems
                )
            }
        }
    }
}