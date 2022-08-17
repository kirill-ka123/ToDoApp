package com.example.todoapp.data.network

import com.example.todoapp.data.network.models.GetItemsResponse
import com.example.todoapp.data.network.models.SetItemsRequest
import com.example.todoapp.data.network.models.UpdateItemRequest
import com.example.todoapp.data.network.models.UpdateItemResponse
import retrofit2.http.*

interface TodoApi {
    @GET("list")
    suspend fun getTodoItems(
    ): GetItemsResponse

    // Не используется
//    @GET("list")
//    suspend fun getTodoItemById(
//        @Query("id")
//        id: String
//    ): Response<GetItemByIdResponse>

    @POST("list")
    suspend fun postTodoItem(
        @Body
        updateItemRequest: UpdateItemRequest
    ): UpdateItemResponse

    @PUT("list/{id}")
    suspend fun putTodoItem(
        @Path("id")
        id: String,
        @Body
        updateItemRequest: UpdateItemRequest
    ): UpdateItemResponse

    @DELETE("list/{id}")
    suspend fun deleteTodoItem(
        @Path("id")
        id: String
    ): UpdateItemResponse

    @PATCH("list")
    suspend fun patchTodoItem(
        @Header("X-Last-Known-Revision")
        revision: String,
        @Body
        setItemsRequest: SetItemsRequest
    ): GetItemsResponse
}