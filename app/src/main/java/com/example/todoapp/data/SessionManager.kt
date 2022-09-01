package com.example.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class SessionManager @Inject constructor(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun saveRevisionNetwork(revision: Int) {
        val editor = prefs.edit()
        editor.putInt(REVISION_NETWORK, revision)
        editor.apply()
    }

    fun fetchRevisionNetwork(): Int {
        return prefs.getInt(REVISION_NETWORK, 0)
    }

    fun saveRevisionDatabase(revision: Int) {
        val editor = prefs.edit()
        editor.putInt(REVISION_DATABASE, revision)
        editor.apply()
    }

    fun fetchRevisionDatabase(): Int {
        return prefs.getInt(REVISION_DATABASE, 0)
    }

    companion object {
        const val REVISION_NETWORK = "revision_network"
        const val REVISION_DATABASE = "revision_database"
        const val SHARED_PREFS_NAME = "shared_prefs_name"
    }
}