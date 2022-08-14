package com.example.todoapp.data.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun saveRevision(revision: Int) {
        val editor = prefs.edit()
        editor.putInt(REVISION, revision)
        editor.apply()
    }

    fun fetchRevision(): Int {
        return prefs.getInt(REVISION, 0)
    }

    companion object {
        const val REVISION = "revision"
        const val SHARED_PREFS_NAME = "shared_prefs_name"
    }
}