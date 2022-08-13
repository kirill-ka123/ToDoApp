package com.example.todoapp.presentation.view

import androidx.recyclerview.widget.DiffUtil
import com.example.todoapp.presentation.models.TodoItem

class TodoItemDiffCalculator : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem && oldItem.done == newItem.done
    }
}