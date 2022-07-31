package com.example.todoapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import kotlinx.android.synthetic.main.todo_item.view.*
import java.text.DateFormat
import java.util.*


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
            if (todoItem.done) {
                tv_title_item.setTextColor(ContextCompat.getColor(context, R.color.label_tertiary))
                tv_title_item.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                iv_importance.visibility = View.GONE
            } else {
                tv_title_item.setTextColor(ContextCompat.getColor(context, R.color.label_primary))
                tv_title_item.paintFlags = 0

                when (todoItem.importance) {
                    Importance.IMPORTANT -> {
                        iv_importance.visibility = View.VISIBLE
                        iv_importance.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_double_mark
                            )
                        )
                    }
                    Importance.LOW -> {
                        iv_importance.visibility = View.VISIBLE
                        iv_importance.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.ic_arrow_down
                            )
                        )
                    }
                    else -> {
                        iv_importance.visibility = View.GONE
                    }
                }
            }

            if (todoItem.deadline != null) {
                tv_date.visibility = View.VISIBLE
                tv_date.text = convertUnixToDate(todoItem.deadline)
            }

            tv_title_item
                .setOnClickListener {
                    onItemClickListener?.let {
                        it(todoItem)
                    }
                }
            iv_info.setOnClickListener {
                onItemClickListener?.let {
                    it(todoItem)
                }
            }
            if (checkbox.isChecked) {
                checkbox.buttonTintList = ContextCompat.getColorStateList(context, R.color.green)
            } else {
                if (todoItem.importance == Importance.IMPORTANT) {
                    checkbox.buttonTintList = ContextCompat.getColorStateList(context, R.color.red)
                } else {
                    checkbox.buttonTintList =
                        ContextCompat.getColorStateList(context, R.color.label_tertiary)
                }
            }
            checkbox.setOnClickListener {
                onCheckboxClickListener?.let {
                    it(todoItem, checkbox.isChecked)
                }
            }
        }
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

    private fun convertUnixToDate(time: Long): String {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = time * 1000
        return DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
    }
}