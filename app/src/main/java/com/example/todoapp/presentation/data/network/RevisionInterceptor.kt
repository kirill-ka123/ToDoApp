package com.example.todoapp.presentation.data.network

import okhttp3.Interceptor
import okhttp3.Response

class RevisionInterceptor(private val sessionManager: SessionManager): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchRevision().let { revision ->
            requestBuilder
                .addHeader("X-Last-Known-Revision", revision.toString())
        }
        return chain.proceed(requestBuilder.build())
    }

}