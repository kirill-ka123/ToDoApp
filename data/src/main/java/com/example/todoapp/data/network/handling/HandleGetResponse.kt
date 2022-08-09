package com.example.todoapp.data.network.handling

import android.content.Context
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.NetworkUtils
import com.example.todoapp.data.network.models.GetItemsResponse
import retrofit2.Response

object HandleGetResponse {
    fun invoke(context: Context, response: Response<GetItemsResponse>): List<TodoItem> {
        return NetworkUtils.callWithResponseCheck(response) { getItemResponse ->
            NetworkUtils.saveRevision(context, getItemResponse.revision)
            modifiedListAfterGetRequest(getItemResponse)
        }
    }

    private fun modifiedListAfterGetRequest(body: GetItemsResponse): List<TodoItem> {
        return body.todoItemsNetwork.map { it.mapToTodoItem() }.sortedBy { todoItem ->
            todoItem.id.toInt()
        }
    }
}