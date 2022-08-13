package com.example.todoapp.presentation.data

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.presentation.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class SessionManager @Inject constructor(context: Context) {
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