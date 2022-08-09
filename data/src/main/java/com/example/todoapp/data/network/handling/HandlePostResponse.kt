package com.example.todoapp.data.network.handling

import android.content.Context
import android.util.Log
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.NetworkUtils
import com.example.todoapp.data.network.models.SetItemResponse
import retrofit2.Response

object HandlePostResponse {
    fun invoke(context: Context, response: Response<SetItemResponse>, oldList: List<TodoItem>): List<TodoItem> {
        return NetworkUtils.callWithResponseCheck(response) { setItemResponse ->
            NetworkUtils.saveRevision(context, setItemResponse.revision)
            modifiedListAfterPostRequest(setItemResponse, oldList)
        }
    }

    private fun modifiedListAfterPostRequest(body: SetItemResponse, oldList: List<TodoItem>): List<TodoItem> {
        val newItem = body.todoItemNetwork.mapToTodoItem()
        val newList = oldList.toMutableList()
        val index = oldList.indexOfFirst{ it.id.toInt() > newItem.id.toInt() }

        if (index == -1) newList.add(newItem)
        else newList.add(index, newItem)

        return newList
    }
}