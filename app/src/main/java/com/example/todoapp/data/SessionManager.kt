package com.example.todoapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.R
import com.example.todoapp.common.Constants.REVISION

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun saveRevision(revision: Int) {
        val editor = prefs.edit()
        editor.putInt(REVISION, revision)
        editor.apply()
    }

    fun fetchRevision(): Int {
        return prefs.getInt(REVISION, 0)
    }
}