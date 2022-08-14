package com.example.todoapp.view

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.di.scopes.AppScope
import com.example.todoapp.models.TodoItem
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