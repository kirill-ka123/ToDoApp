package com.example.todoapp.view

import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.common.Utils
import com.example.todoapp.databinding.TodoItemBinding
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem

class TodoViewHolder(private val itemBinding: TodoItemBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(
        todoItem: TodoItem,
        onItemClickListener: ((TodoItem) -> Unit)?,
        onCheckboxClickListener: ((TodoItem, Boolean) -> Unit)?
    ) {
        itemBinding.apply {
            checkbox.isChecked = todoItem.done
            tvTitleItem.text = todoItem.text
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
        itemBinding.apply {
            tvTitleItem.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    R.color.label_tertiary
                )
            )
            tvTitleItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            ivImportance.visibility = View.GONE
        }
    }

    private fun setTodoItemNotDone() {
        itemBinding.apply {
            tvTitleItem.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    R.color.label_primary
                )
            )
            tvTitleItem.paintFlags = 0
        }
    }

    private fun setTodoItemHighImportance() {
        itemBinding.apply {
            ivImportance.visibility = View.VISIBLE
            ivImportance.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    R.drawable.ic_double_mark
                )
            )
        }
    }

    private fun setTodoItemLowImportance() {
        itemBinding.apply {
            ivImportance.visibility = View.VISIBLE
            ivImportance.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    R.drawable.ic_arrow_down
                )
            )
        }
    }

    private fun setTodoItemBasicImportance() {
        itemBinding.apply {
            ivImportance.visibility = View.GONE
        }
    }

    private fun setTodoItemGreenCheckbox() {
        itemBinding.apply {
            checkbox.isChecked = true
            checkbox.buttonTintList =
                ContextCompat.getColorStateList(root.context, R.color.green)
        }
    }

    private fun setTodoItemRedCheckbox() {
        itemBinding.apply {
            checkbox.isChecked = false
            checkbox.buttonTintList =
                ContextCompat.getColorStateList(root.context, R.color.red)
        }
    }

    private fun setTodoItemGreyCheckbox() {
        itemBinding.apply {
            checkbox.isChecked = false
            checkbox.buttonTintList =
                ContextCompat.getColorStateList(root.context, R.color.label_tertiary)
        }
    }

    private fun setOnClickListeners(
        todoItem: TodoItem, onItemClickListener: ((TodoItem) -> Unit)?,
        onCheckboxClickListener: ((TodoItem, Boolean) -> Unit)?
    ) {
        itemBinding.apply {
            tvTitleItem
                .setOnClickListener {
                    onItemClickListener?.let {
                        it(todoItem)
                    }
                }
            ivInfo.setOnClickListener {
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