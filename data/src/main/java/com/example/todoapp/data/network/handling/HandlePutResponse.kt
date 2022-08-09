package com.example.todoapp.data.network.handling

import android.content.Context
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.NetworkUtils
import com.example.todoapp.data.network.models.SetItemResponse
import retrofit2.Response

object HandlePutResponse {
    fun invoke(context: Context, response: Response<SetItemResponse>, oldList: List<TodoItem>): List<TodoItem> {
        return NetworkUtils.callWithResponseCheck(response) { setItemResponse ->
            NetworkUtils.saveRevision(context, setItemResponse.revision)
            modifiedListAfterPutRequest(setItemResponse, oldList)
        }
    }

    private fun modifiedListAfterPutRequest(body: SetItemResponse, oldList: List<TodoItem>): List<TodoItem> {
        val newItem = body.todoItemNetwork.mapToTodoItem()
        return oldList.map {
            if (it.id == newItem.id) {
                newItem
            } else it
        }
    }
}