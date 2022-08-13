package com.example.todoapp.presentation.data.network

import com.example.todoapp.presentation.data.network.models.GetItemsResponse
import com.example.todoapp.presentation.data.network.models.SetItemRequest
import com.example.todoapp.presentation.data.network.models.SetItemResponse
import retrofit2.Response
import retrofit2.http.*

interface TodoApi {
    @GET("list")
    suspend fun getTodoItems(
    ): Response<GetItemsResponse>

    // Не используется
//    @GET("list")
//    suspend fun getTodoItemById(
//        @Query("id")
//        id: String
//    ): Response<GetItemByIdResponse>

    @POST("list")
    suspend fun postTodoItem(
        @Body
        setItemRequest: SetItemRequest
    ): Response<SetItemResponse>

    @PUT("list/{id}")
    suspend fun putTodoItem(
        @Path("id")
        id: String,
        @Body
        setItemRequest: SetItemRequest
    ): Response<SetItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Path("id")
        id: String
    ): Response<SetItemResponse>
}