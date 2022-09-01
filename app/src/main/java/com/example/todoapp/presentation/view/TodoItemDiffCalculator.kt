package com.example.todoapp.presentation.view

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.data.db.models.TodoItem
import com.example.todoapp.di.scopes.AppScope
import javax.inject.Inject

@AppScope
class TodoItemDiffCalculator @Inject constructor() : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem && oldItem.done == newItem.done
    }
}