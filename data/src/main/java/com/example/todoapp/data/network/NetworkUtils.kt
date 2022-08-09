package com.example.todoapp.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.todoapp.data.SessionManager
import com.example.todoapp.data.models.TodoItem
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

object NetworkUtils {
    fun saveRevision(context: Context, revision: Int) {
        SessionManager(context).saveRevision(revision)
    }

    suspend fun <T> callWithInternetCheck(
        context: Context,
        call: suspend () -> (Response<T>)
    ): Response<T> {
        if (hasInternetConnection(context)) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    fun <T> callWithResponseCheck(
        response: Response<T>,
        call: (T) -> (List<TodoItem>)
    ): List<TodoItem> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return call(body)
            } else throw UnknownHostException("Тело запроса - null")
        } else throw UnknownHostException(response.message())
    }

    private fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}