package com.example.todoapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.models.TodoItem
import kotlinx.android.synthetic.main.todo_item.view.*

class CasesAdapter : RecyclerView.Adapter<CasesAdapter.CasesViewHolder>() {
    class CasesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CasesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.todo_item, parent, false)
        return CasesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CasesViewHolder, position: Int) {
        val todoItem = differ.currentList[position]

        holder.itemView.apply {
            checkbox.isChecked = todoItem.done
            tv_title_item.text = todoItem.text
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}