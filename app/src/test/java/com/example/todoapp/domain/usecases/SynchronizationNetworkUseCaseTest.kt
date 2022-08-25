package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SynchronizationNetworkUseCaseTest {
    private lateinit var testTodoItemsFromDatabase: MutableList<TodoItem>

    private lateinit var synchronizationNetworkUseCase: SynchronizationNetworkUseCase
    private val todoApi: TodoApi = mock()
    private val sessionManager: SessionManager = mock()
    private val checkInternet: CheckInternet = mock()

    @Before
    fun setUp() {
        testTodoItemsFromDatabase = mutableListOf()
        ('a'..'z').forEachIndexed { index, c ->
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

        synchronizationNetworkUseCase = SynchronizationNetworkUseCase(
            todoApi,
            sessionManager,
            checkInternet
        )
    }

    @After
    fun tearDown() {
        reset(todoApi)
        reset(sessionManager)
        reset(checkInternet)
    }

    @Test
    fun `should get todoItems from network and synchronize data`() {
        val testDatabaseRevision = 20
        val testTodoItemsNetwork = testTodoItemsFromDatabase.map { it.mapToTodoItemNetwork() }
        val setItemsRequest = SetItemsRequest("ok", testTodoItemsNetwork)
        val getItemsResponse =
            GetItemsResponse("ok", testTodoItemsNetwork, testDatabaseRevision)

        runBlocking {
            // Проверка без интернета и с интернетом
            for (hasInternetConnection in 0..1) {
                whenever(
                    todoApi.patchTodoItem(
                        (testDatabaseRevision - 1).toString(),
                        setItemsRequest
                    )
                ).thenReturn(getItemsResponse)
                whenever(checkInternet.hasInternetConnection()).thenReturn(hasInternetConnection == 1)
                whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testDatabaseRevision)

                synchronizationNetworkUseCase.synchronizeNetwork(testTodoItemsFromDatabase)

                // Проверяем, что patch запрос вызывается в зависимости от наличия интернета
                verify(todoApi, times(hasInternetConnection))
                    .patchTodoItem((testDatabaseRevision - 1).toString(), setItemsRequest)

                // Проверяем, что после запроса в сеть, вызывается метод сохранения ревизии сети
                verify(sessionManager, times(hasInternetConnection))
                    .saveRevisionNetwork(testDatabaseRevision)
            }
        }
    }
}