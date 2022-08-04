package com.example.todoapp.network

import com.example.todoapp.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface TodoApi {
    @GET("list")
    suspend fun getTodoItems(
    ): Response<GetItemsResponse>

    @GET("list")
    suspend fun getTodoItemById(
        @Query("id")
        id: String
    ): Response<GetItemByIdResponse>

    @POST("list")
    suspend fun postTodoItem(
        @Body
        postItemRequest: SetItemRequest
    ): Response<SetItemResponse>

    @PUT("list/{id}")
    suspend fun putTodoItem(
        @Path("id")
        id: String,
        @Body
        todoItem: SetItemRequest
    ): Response<SetItemResponse>

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Path("id")
        id: String
    ): Response<SetItemResponse>
}