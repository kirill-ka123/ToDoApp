package com.example.todoapp.data.network

import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.network.models.SetItemResponse
import com.example.todoapp.di.scopes.AppScope
import com.example.todoapp.models.TodoItem
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject

@AppScope
class HandleResponses @Inject constructor(
    private val sessionManager: SessionManager
) {
    private fun <T> callWithResponseCheck(
        response: Response<T>,
        call: (T) -> (List<TodoItem>)
    ): List<TodoItem> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return call(body)
            } else throw UnknownHostException("Тело запроса - null")
        } else throw UnknownHostException(response.message())
    }

    fun handleGetResponse(response: Response<GetItemsResponse>): List<TodoItem> {
        return callWithResponseCheck(response) { getItemResponse ->
            sessionManager.saveRevision(getItemResponse.revision)
            modifiedListAfterGetRequest(getItemResponse)
        }
    }

    private fun modifiedListAfterGetRequest(body: GetItemsResponse): List<TodoItem> {
        return body.todoItemsNetwork.map { it.mapToTodoItem() }.sortedBy { todoItem ->
            todoItem.id.toInt()
        }
    }

    fun handlePostResponse(
        response: Response<SetItemResponse>,
        oldList: List<TodoItem>
    ): List<TodoItem> {
        return callWithResponseCheck(response) { setItemResponse ->
            sessionManager.saveRevision(setItemResponse.revision)
            modifiedListAfterPostRequest(setItemResponse, oldList)
        }
    }

    private fun modifiedListAfterPostRequest(
        body: SetItemResponse,
        oldList: List<TodoItem>
    ): List<TodoItem> {
        val newItem = body.todoItemNetwork.mapToTodoItem()
        val newList = oldList.toMutableList()
        val index = oldList.indexOfFirst { it.id.toInt() > newItem.id.toInt() }

        if (index == -1) newList.add(newItem)
        else newList.add(index, newItem)

        return newList
    }

    fun handlePutResponse(
        response: Response<SetItemResponse>,
        oldList: List<TodoItem>
    ): List<TodoItem> {
        return callWithResponseCheck(response) { setItemResponse ->
            sessionManager.saveRevision(setItemResponse.revision)
            modifiedListAfterPutRequest(setItemResponse, oldList)
        }
    }

    private fun modifiedListAfterPutRequest(
        body: SetItemResponse,
        oldList: List<TodoItem>
    ): List<TodoItem> {
        val newItem = body.todoItemNetwork.mapToTodoItem()
        return oldList.map {
            if (it.id == newItem.id) {
                newItem
            } else it
        }
    }

    fun handleDeleteResponse(
        response: Response<SetItemResponse>,
        oldList: List<TodoItem>
    ): List<TodoItem> {
        return callWithResponseCheck(response) { setItemResponse ->
            sessionManager.saveRevision(setItemResponse.revision)
            modifiedListAfterDeleteRequest(setItemResponse, oldList)
        }
    }

    private fun modifiedListAfterDeleteRequest(
        setItemResponse: SetItemResponse,
        oldList: List<TodoItem>
    ): List<TodoItem> {
        val deletedItem = setItemResponse.todoItemNetwork.mapToTodoItem()
        return oldList.filter { it.id != deletedItem.id }
    }
}