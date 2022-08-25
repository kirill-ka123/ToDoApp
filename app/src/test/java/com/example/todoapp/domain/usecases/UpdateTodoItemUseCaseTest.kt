package com.example.todoapp.domain.usecases

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.CheckInternet
import com.example.todoapp.data.network.PrepareRequests
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.data.network.models.UpdateItemRequest
import com.example.todoapp.data.network.models.UpdateItemResponse
import com.example.todoapp.data.repository.TodoItemsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class UpdateTodoItemUseCaseTest {
    private lateinit var testTodoItem: TodoItem

    private lateinit var updateTodoItemUseCase: UpdateTodoItemUseCase
    private val todoItemsDao: TodoItemsDao = mock()
    private val todoApi: TodoApi = mock()
    private val sessionManager: SessionManager = mock()
    private val checkInternet: CheckInternet = mock()

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

        val prepareRequests = PrepareRequests()
        val todoItemsRepository = TodoItemsRepository(
            todoItemsDao,
            todoApi
        )
        updateTodoItemUseCase = UpdateTodoItemUseCase(
            todoItemsRepository,
            sessionManager,
            prepareRequests,
            checkInternet
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
    fun `should add todoItem in network and increase network revision`() {
        val testNetworkRevision = 20
        val testTodoItemNetwork = testTodoItem.mapToTodoItemNetwork()
        val testUpdateItemRequest = UpdateItemRequest(todoItemNetwork = testTodoItemNetwork)
        val testUpdateItemResponse =
            UpdateItemResponse("ok", testTodoItemNetwork, testNetworkRevision)

        runBlocking {
            // Проверка без интернета и с интернетом
            for (hasInternetConnection in 0..1) {
                whenever(checkInternet.hasInternetConnection()).thenReturn(hasInternetConnection == 1)
                whenever(todoApi.postTodoItem(testUpdateItemRequest))
                    .thenReturn(testUpdateItemResponse)

                updateTodoItemUseCase.addTodoItem(testTodoItem, testTodoItem.id)

                // Проверяем, что запрос добавления дела в облако вызывается в зависимости от наличия интернета
                verify(todoApi, times(hasInternetConnection)).postTodoItem(testUpdateItemRequest)

                // Проверяем, что после запроса в сеть, вызывается метод сохранения ревизии сети
                verify(sessionManager, times(hasInternetConnection)).saveRevisionNetwork(
                    testNetworkRevision
                )
            }
        }
    }

    @Test
    fun `should add todoItem with a known id in database and increase database revision`() {
        val testOldDatabaseRevision = 10
        val testNewDatabaseRevision = 11

        runBlocking {
            whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testOldDatabaseRevision)

            updateTodoItemUseCase.addTodoItem(testTodoItem, testTodoItem.id)

            // Проверяем, что метод добавления дела в базу данных вызывается
            verify(todoItemsDao).addTodoItem(testTodoItem)

            // Проверяем, что методы чтения и сохранения ревизии базы данных вызываются и
            // ревизия базы данных увеличивается на 1
            verify(sessionManager).fetchRevisionDatabase()
            verify(sessionManager).saveRevisionDatabase(testNewDatabaseRevision)
        }
    }

    @Test
    fun `should add todoItem with a unknown id in database and increase database revision`() {
        val testOldDatabaseRevision = 10
        val testNewDatabaseRevision = 11

        runBlocking {
            whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testOldDatabaseRevision)

            updateTodoItemUseCase.addTodoItem(testTodoItem, null)

            // Проверяем, что метод добавления дела в базу данных вызывается
            verify(todoItemsDao).addTodoItem(anyOrNull())

            // Проверяем, что методы чтения и сохранения ревизии базы данных вызываются и
            // ревизия базы данных увеличивается на 1
            verify(sessionManager).fetchRevisionDatabase()
            verify(sessionManager).saveRevisionDatabase(testNewDatabaseRevision)
        }
    }

    @Test
    fun `should edit todoItem in network and increase network revision`() {
        val testNetworkRevision = 20
        val testTodoItemNetwork = testTodoItem.mapToTodoItemNetwork()
        val testUpdateItemRequest = UpdateItemRequest(todoItemNetwork = testTodoItemNetwork)
        val testUpdateItemResponse =
            UpdateItemResponse("ok", testTodoItemNetwork, testNetworkRevision)

        runBlocking {
            // Проверка без интернета и с интернетом
            for (hasInternetConnection in 0..1) {
                whenever(checkInternet.hasInternetConnection()).thenReturn(hasInternetConnection == 1)
                whenever(todoApi.putTodoItem(testTodoItemNetwork.id, testUpdateItemRequest))
                    .thenReturn(testUpdateItemResponse)

                updateTodoItemUseCase.editTodoItem(testTodoItem)

                // Проверяем, что запрос добавления дела в облако вызывается в зависимости от наличия интернета
                verify(todoApi, times(hasInternetConnection)).putTodoItem(
                    testTodoItemNetwork.id,
                    testUpdateItemRequest
                )

                // Проверяем, что после запроса в сеть, вызывается метод сохранения ревизии сети
                verify(sessionManager, times(hasInternetConnection)).saveRevisionNetwork(
                    testNetworkRevision
                )
            }
        }
    }

    @Test
    fun `should edit todoItem in database and increase database revision`() {
        val testOldDatabaseRevision = 10
        val testNewDatabaseRevision = 11

        runBlocking {
            whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testOldDatabaseRevision)

            updateTodoItemUseCase.editTodoItem(testTodoItem)

            // Проверяем, что метод изменения дела в базе данных вызывается
            verify(todoItemsDao).editTodoItem(testTodoItem)

            // Проверяем, что методы чтения и сохранения ревизии базы данных вызываются и
            // ревизия базы данных увеличивается на 1
            verify(sessionManager).fetchRevisionDatabase()
            verify(sessionManager).saveRevisionDatabase(testNewDatabaseRevision)
        }
    }

    @Test
    fun `should delete todoItem from network and increase network revision`() {
        val testNetworkRevision = 20
        val testTodoItemNetwork = testTodoItem.mapToTodoItemNetwork()
        val testUpdateItemResponse =
            UpdateItemResponse("ok", testTodoItemNetwork, testNetworkRevision)

        runBlocking {
            // Проверка без интернета и с интернетом
            for (hasInternetConnection in 0..1) {
                whenever(checkInternet.hasInternetConnection()).thenReturn(hasInternetConnection == 1)
                whenever(todoApi.deleteTodoItem(testTodoItemNetwork.id))
                    .thenReturn(testUpdateItemResponse)

                updateTodoItemUseCase.deleteTodoItem(testTodoItem)

                // Проверяем, что запрос добавления дела в облако вызывается в зависимости от наличия интернета
                verify(todoApi, times(hasInternetConnection)).deleteTodoItem(testTodoItemNetwork.id)

                // Проверяем, что после запроса в сеть, вызывается метод сохранения ревизии сети
                verify(sessionManager, times(hasInternetConnection)).saveRevisionNetwork(
                    testNetworkRevision
                )
            }
        }
    }

    @Test
    fun `should delete todoItem from database and increase database revision`() {
        val testOldDatabaseRevision = 10
        val testNewDatabaseRevision = 11

        runBlocking {
            whenever(sessionManager.fetchRevisionDatabase()).thenReturn(testOldDatabaseRevision)

            updateTodoItemUseCase.deleteTodoItem(testTodoItem)

            // Проверяем, что метод удаления дела из базы данных вызывается
            verify(todoItemsDao).deleteTodoItem(testTodoItem)

            // Проверяем, что методы чтения и сохранения ревизии базы данных вызываются и
            // ревизия базы данных увеличивается на 1
            verify(sessionManager).fetchRevisionDatabase()
            verify(sessionManager).saveRevisionDatabase(testNewDatabaseRevision)
        }
    }
}