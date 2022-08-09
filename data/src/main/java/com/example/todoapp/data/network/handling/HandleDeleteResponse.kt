package com.example.todoapp.data.network.handling

import android.content.Context
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.data.network.NetworkUtils
import com.example.todoapp.data.network.models.SetItemResponse
import retrofit2.Response

object HandleDeleteResponse {
    fun invoke(context: Context, response: Response<SetItemResponse>, oldList: List<TodoItem>): List<TodoItem> {
        return NetworkUtils.callWithResponseCheck(response) { setItemResponse ->
            NetworkUtils.saveRevision(context, setItemResponse.revision)
            modifiedListAfterDeleteRequest(setItemResponse, oldList)
        }
    }

    private fun modifiedListAfterDeleteRequest(setItemResponse: SetItemResponse, oldList: List<TodoItem>): List<TodoItem> {
        val deletedItem = setItemResponse.todoItemNetwork.mapToTodoItem()
        return oldList.filter { it.id != deletedItem.id }
    }
}