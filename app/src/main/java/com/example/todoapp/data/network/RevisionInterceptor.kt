package com.example.todoapp.data.network

import com.example.todoapp.data.SessionManager
import com.example.todoapp.di.scopes.AppScope
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

@AppScope
class RevisionInterceptor @Inject constructor(private val sessionManager: SessionManager) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = if (isUpdateMethod(request.method)) {
            request.newBuilder().addHeader(
                "X-Last-Known-Revision",
                sessionManager.fetchRevisionNetwork().toString()
            ).build()
        } else request

        return chain.proceed(newRequest)
    }

    private fun isUpdateMethod(method: String): Boolean =
        method == HTTP_METHOD_POST || method == HTTP_METHOD_PUT || method == HTTP_METHOD_DELETE

    companion object {
        const val HTTP_METHOD_POST = "POST"
        const val HTTP_METHOD_PUT = "PUT"
        const val HTTP_METHOD_DELETE = "DELETE"
    }
}