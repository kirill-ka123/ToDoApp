package com.example.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.data.common.Constants.REVISION
import com.example.todoapp.data.common.Constants.SHARED_PREFS_NAME

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
}