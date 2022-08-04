package com.example.todoapp.network

import android.content.Context
import android.util.Log
import com.example.todoapp.common.Constants
import com.example.todoapp.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class CustomInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder =
            chain.request().newBuilder().addHeader("Authorization", "Bearer ${Constants.TOKEN}")

        sessionManager.fetchRevision().let { revision ->
            requestBuilder
                .addHeader("X-Last-Known-Revision", revision.toString())
        }

        return chain.proceed(requestBuilder.build())
    }
}