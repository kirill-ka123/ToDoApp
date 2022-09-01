package com.example.todoapp.common

import com.example.todoapp.data.db.models.Importance
import com.example.todoapp.data.network.CheckInternet
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.io.IOException
import java.text.DateFormat
import java.util.*

object Utils {
    fun convertStringIdToImportance(selectedItemPosition: Int) =
        when (selectedItemPosition) {
            0 -> Importance.BASIC
            1 -> Importance.LOW
            2 -> Importance.IMPORTANT
            else -> {
                throw IllegalArgumentException()
            }
        }

    fun convertUnixToDate(time: Long): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time * 1000
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
    }

    suspend fun callWithRetry(
        call: suspend () -> (Unit),
        actionAfterErroneousCall: (Throwable) -> (Unit)
    ) {
        var retryCounter = 0
        while (currentCoroutineContext().isActive && retryCounter <= RETRY_COUNT) {
            try {
                call()
                break
            } catch (t: Throwable) {
                if (retryCounter == RETRY_COUNT) {
                    actionAfterErroneousCall(t)
                }
                retryCounter += 1
                delay(NETWORK_RETRY_DELAY)
            }
        }
    }

    suspend fun <T> callWithInternetCheck(
        checkInternet: CheckInternet,
        call: suspend () -> (T)
    ): T {
        if (checkInternet.hasInternetConnection()) {
            return call()
        } else throw IOException("Нет интернет соединения")
    }

    private const val RETRY_COUNT = 1
    private const val NETWORK_RETRY_DELAY = 1000L
}