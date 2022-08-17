package com.example.todoapp.data.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.todoapp.di.scopes.AppScope
import java.io.IOException
import javax.inject.Inject

@AppScope
class CheckInternet @Inject constructor(private val connectivityManager: ConnectivityManager) {
    suspend fun <T> callWithInternetCheck(
        call: suspend () -> (T)
    ): T {
        if (hasInternetConnection()) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    fun hasInternetConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}