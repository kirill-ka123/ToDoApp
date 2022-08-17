package com.example.todoapp.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.todoapp.data.db.ConverterImportance
import kotlinx.parcelize.Parcelize

@Entity(tableName = "todoItems")
@TypeConverters(ConverterImportance::class)
@Parcelize
data class TodoItem(
    @PrimaryKey
    val id: Int,
    val text: String?,
    val importance: Importance?,
    val deadline: Long? = 0L,
    val done: Boolean?,
    val createdAt: Long?,
    val changedAt: Long? = 0L
) : Parcelable