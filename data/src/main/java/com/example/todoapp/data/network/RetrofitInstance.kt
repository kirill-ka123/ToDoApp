package com.example.todoapp.data.network

import android.content.Context
import com.example.todoapp.data.common.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    @Volatile
    private var instance: TodoApi? = null
    private val lock = Any()

    fun getApi(context: Context) = instance ?: synchronized(lock) {
        instance ?: createApi(context).also { instance = it }
    }

    private fun createApi(context: Context): TodoApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttpClient(context))
            .build()

        return retrofit.create(TodoApi::class.java)
    }

    private fun okhttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(CustomInterceptor(context))
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}