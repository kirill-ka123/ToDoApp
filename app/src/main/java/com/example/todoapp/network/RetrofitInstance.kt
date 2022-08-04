package com.example.todoapp.network

import android.content.Context
import com.example.todoapp.common.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private lateinit var api: TodoApi

    fun getApi(context: Context): TodoApi {
        if (!::api.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttpClient(context))
                .build()

            api = retrofit.create(TodoApi::class.java)
        }

        return api
    }

    private fun okhttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(CustomInterceptor(context))
            .build()
    }
}