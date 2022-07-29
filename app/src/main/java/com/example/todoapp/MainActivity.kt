package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todoapp.repository.TodoItemsRepository

class MainActivity : AppCompatActivity() {
    lateinit var todoItemsRepository: TodoItemsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        todoItemsRepository = TodoItemsRepository()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}