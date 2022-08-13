package com.example.todoapp.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.presentation.models.TodoItem

class TodoAdapter(todoItemDiffCalculator: TodoItemDiffCalculator) : RecyclerView.Adapter<TodoViewHolder>() {
    val differ = AsyncListDiffer(this, todoItemDiffCalculator)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(differ.currentList[position], onItemClickListener, onCheckboxClickListener)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((TodoItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (TodoItem) -> Unit) {
        onItemClickListener = listener
    }

    private var onCheckboxClickListener: ((TodoItem, isChecked: Boolean) -> Unit)? = null

    fun setOnCheckboxClickListener(listener: (TodoItem, isChecked: Boolean) -> Unit) {
        onCheckboxClickListener = listener
    }
}