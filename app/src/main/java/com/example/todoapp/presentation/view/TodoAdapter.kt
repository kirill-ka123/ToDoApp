package com.example.todoapp.presentation.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.TodoItemBinding
import com.example.todoapp.presentation.di.scopes.AppScope
import com.example.todoapp.presentation.di.scopes.TodoFragmentScope
import com.example.todoapp.presentation.models.TodoItem
import javax.inject.Inject

@AppScope
class TodoAdapter @Inject constructor(todoItemDiffCalculator: TodoItemDiffCalculator) : RecyclerView.Adapter<TodoViewHolder>() {
    val differ = AsyncListDiffer(this, todoItemDiffCalculator)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemBinding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(itemBinding)
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