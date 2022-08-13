package com.example.todoapp.presentation.data.network

import com.example.todoapp.presentation.di.scopes.AppScope
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

@AppScope
class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $TOKEN")

        return chain.proceed(requestBuilder.build())
    }

    companion object {
        const val TOKEN = "LethonelRavavaris"
    }
}