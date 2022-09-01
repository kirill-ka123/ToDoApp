package com.example.todoapp.data.db

import androidx.room.TypeConverter
import com.example.todoapp.data.db.models.Importance

class ConverterImportance {
    @TypeConverter
    fun fromImportance(importance: Importance): String {
        return importance.name
    }

    @TypeConverter
    fun toImportance(name: String): Importance {
        return try {
            Importance.valueOf(name)
        } catch (e: Exception) {
            Importance.BASIC
        }
    }
}