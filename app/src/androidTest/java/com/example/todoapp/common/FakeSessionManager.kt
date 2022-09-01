package com.example.todoapp.common

import android.content.Context
import com.example.todoapp.data.SessionManager

class FakeSessionManager(context: Context) : SessionManager(context) {
    private var networkRevision = 0
    private var databaseRevision = 0

    override fun saveRevisionNetwork(revision: Int) {
        networkRevision = revision
    }

    override fun fetchRevisionNetwork() = networkRevision

    override fun saveRevisionDatabase(revision: Int) {
        databaseRevision = revision
    }

    override fun fetchRevisionDatabase() = databaseRevision
}