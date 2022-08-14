package com.example.todoapp.data.network

import com.example.todoapp.data.SessionManager
import com.example.todoapp.di.scopes.AppScope
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

@AppScope
class RevisionInterceptor @Inject constructor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchRevision().let { revision ->
            requestBuilder
                .addHeader("X-Last-Known-Revision", revision.toString())
        }
        return chain.proceed(requestBuilder.build())
    }
}