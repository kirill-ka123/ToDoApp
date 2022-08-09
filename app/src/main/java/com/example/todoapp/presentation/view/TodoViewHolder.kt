package com.example.todoapp.presentation.view

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.data.models.Importance
import com.example.todoapp.data.models.TodoItem
import com.example.todoapp.presentation.common.Utils
import kotlinx.android.synthetic.main.todo_item.view.*

class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        todoItem: TodoItem,
        onItemClickListener: ((TodoItem) -> Unit)?,
        onCheckboxClickListener: ((TodoItem, Boolean) -> Unit)?
    ) {
        itemView.apply {
            checkbox.isChecked = todoItem.done
            tv_title_item.text = todoItem.text
            if (todoItem.done) {
                setTodoItemDone()
            } else {
                setTodoItemNotDone()

                when (todoItem.importance) {
                    Importance.IMPORTANT -> {
                        setTodoItemRedCheckbox()
                        setTodoItemHighImportance()
                    }
                    Importance.LOW -> {
                        setTodoItemLowImportance()
                        setTodoItemGreyCheckbox()
                    }
                    Importance.BASIC -> {
                        setTodoItemBasicImportance()
                        setTodoItemGreyCheckbox()
                    }
                }
            }
            if (todoItem.deadline > 0L) {
                tvDate.visibility = View.VISIBLE
                tvDate.text = Utils.convertUnixToDate(todoItem.deadline)
            } else {
                tvDate.visibility = View.GONE
                tvDate.text = ""
            }
            setOnClickListeners(todoItem, onItemClickListener, onCheckboxClickListener)
        }
    }

    private fun setTodoItemDone() {
        setTodoItemGreenCheckbox()
        itemView.apply {
            tv_title_item.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.label_tertiary
                )
            )
            tv_title_item.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            iv_importance.visibility = View.GONE
        }
    }

    private fun setTodoItemNotDone() {
        itemView.apply {
            tv_title_item.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.label_primary
                )
            )
            tv_title_item.paintFlags = 0
        }
    }

    private fun setTodoItemHighImportance() {
        itemView.apply {
            iv_importance.visibility = View.VISIBLE
            iv_importance.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_double_mark
                )
            )
        }
    }

    private fun setTodoItemLowImportance() {
        itemView.apply {
            iv_importance.visibility = View.VISIBLE
            iv_importance.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_arrow_down
                )
            )
        }
    }

    private fun setTodoItemBasicImportance() {
        itemView.apply {
            iv_importance.visibility = View.GONE
        }
    }

    private fun setTodoItemGreenCheckbox() {
        itemView.apply {
            checkbox.isChecked = true
            checkbox.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.green)
        }
    }

    private fun setTodoItemRedCheckbox() {
        itemView.apply {
            checkbox.isChecked = false
            checkbox.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.red)
        }
    }

    private fun setTodoItemGreyCheckbox() {
        itemView.apply {
            checkbox.isChecked = false
            checkbox.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.label_tertiary)
        }
    }

    private fun setOnClickListeners(
        todoItem: TodoItem, onItemClickListener: ((TodoItem) -> Unit)?,
        onCheckboxClickListener: ((TodoItem, Boolean) -> Unit)?
    ) {
        itemView.apply {
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
            checkbox.setOnClickListener {
                onCheckboxClickListener?.let {
                    it(todoItem, checkbox.isChecked)
                }
            }
        }
    }
}