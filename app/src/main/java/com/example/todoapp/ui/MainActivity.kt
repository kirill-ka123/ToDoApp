package com.example.todoapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.R
import com.example.todoapp.data.SourceData
import com.example.todoapp.repository.TodoItemsRepository
import com.example.todoapp.ui.viewModel.TodoViewModel
import com.example.todoapp.ui.viewModel.TodoViewModelFactory

class MainActivity : AppCompatActivity() {
    val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(TodoItemsRepository(SourceData.todoItems))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}