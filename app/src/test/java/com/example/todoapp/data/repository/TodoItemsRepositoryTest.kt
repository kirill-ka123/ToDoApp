package com.example.todoapp.data.repository

import com.example.todoapp.data.db.TodoItemsDao
import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.data.network.TodoApi
import com.example.todoapp.data.network.models.TodoItemNetwork.Companion.mapToTodoItemNetwork
import com.example.todoapp.data.network.models.UpdateItemRequest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TodoItemsRepositoryTest {
    private lateinit var testTodoItem: TodoItem

    private lateinit var todoItemsRepository: TodoItemsRepository
    private val todoItemsDao = mock<TodoItemsDao>()
    private val todoApi = mock<TodoApi>()

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

        todoItemsRepository = TodoItemsRepository(
            todoItemsDao,
            todoApi
        )
    }

    @After
    fun tearDown() {
        reset(todoItemsDao)
        reset(todoApi)
    }

    @Test
    fun `should get todoItems in LiveData from database`() {
        todoItemsRepository.getTodoItemsLivaData()

        verify(todoItemsDao).getAllTodoItemsLive()
    }

    @Test
    fun `should get todoItems from database`() {
        val testTodoItems = mutableListOf<TodoItem>()
        ('a'..'z').forEachIndexed { index, c ->
            testTodoItems.add(
                TodoItem(
                    id = index.toString(),
                    text = c.toString(),
                    importance = Importance.IMPORTANT,
                    done = index % 2 == 1,
                    createdAt = (0L..5L).random(),
                    changedAt = (index + 1).toLong()
                )
            )
        }
        testTodoItems.shuffle()

        runBlocking {
            whenever(todoItemsDao.getAllTodoItems()).thenReturn(testTodoItems)

            val todoItems = todoItemsRepository.getTodoItemsDatabase()

            verify(todoItemsDao).getAllTodoItems()

            for (i in 0 until todoItems.size - 1) {
                assert(todoItems[i].createdAt!! <= todoItems[i + 1].createdAt!!)
            }
        }
    }

    @Test
    fun `should add todoItem in database`() {
        runBlocking {
            todoItemsRepository.addTodoItemDatabase(testTodoItem)

            verify(todoItemsDao).addTodoItem(testTodoItem)
        }
    }

    @Test
    fun `should edit todoItem in database`() {
        runBlocking {
            todoItemsRepository.editTodoItemDatabase(testTodoItem)

            verify(todoItemsDao).editTodoItem(testTodoItem)
        }
    }

    @Test
    fun `should delete todoItem in database`() {
        runBlocking {
            todoItemsRepository.deleteTodoItemDatabase(testTodoItem)

            verify(todoItemsDao).deleteTodoItem(testTodoItem)
        }
    }

    @Test
    fun `should get todoItems from network`() {
        runBlocking {
            todoItemsRepository.getTodoItemsNetwork()

            verify(todoApi).getTodoItems()
        }
    }

    @Test
    fun `should add todoItem in network`() {
        val testTodoItemNetwork = testTodoItem.mapToTodoItemNetwork()
        val updateItemRequest = UpdateItemRequest(todoItemNetwork = testTodoItemNetwork)
        runBlocking {
            todoItemsRepository.postTodoItemNetwork(updateItemRequest)

            verify(todoApi).postTodoItem(updateItemRequest)
        }
    }

    @Test
    fun `should edit todoItem in network`() {
        val testTodoItemNetwork = testTodoItem.mapToTodoItemNetwork()
        val updateItemRequest = UpdateItemRequest(todoItemNetwork = testTodoItemNetwork)
        runBlocking {
            todoItemsRepository.putTodoItemNetwork(testTodoItemNetwork.id, updateItemRequest)

            verify(todoApi).putTodoItem(testTodoItemNetwork.id, updateItemRequest)
        }
    }

    @Test
    fun `should delete todoItem in network`() {
        runBlocking {
            todoItemsRepository.deleteTodoItemNetwork(testTodoItem.id)

            verify(todoApi).deleteTodoItem(testTodoItem.id)
        }
    }
}