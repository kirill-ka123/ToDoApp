package com.example.todoapp.view

import android.content.res.ColorStateList
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
            checkbox.isChecked = todoItem.done == true
            tvTitleItem.text = todoItem.text
            if (todoItem.done == true) {
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
            if (todoItem.deadline != null && todoItem.deadline > 0L) {
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
            tvTitleItem.setTextColor(root.context.getColorFromAttr(android.R.attr.textColorTertiary))
            tvTitleItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            ivImportance.visibility = View.GONE
        }
    }

    private fun setTodoItemNotDone() {
        itemBinding.apply {
            tvTitleItem.setTextColor(root.context.getColorFromAttr(android.R.attr.textColorPrimary))
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
                ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                    intArrayOf(
                        root.context.getColorFromAttr(androidx.appcompat.R.attr.colorControlActivated),
                        root.context.getColorFromAttr(androidx.appcompat.R.attr.colorControlActivated)
                    )
                )
        }
    }

    private fun setTodoItemRedCheckbox() {
        itemBinding.apply {
            checkbox.isChecked = false
            checkbox.buttonTintList =
                ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                    intArrayOf(
                        root.context.getColorFromAttr(androidx.appcompat.R.attr.colorError),
                        root.context.getColorFromAttr(androidx.appcompat.R.attr.colorError)
                    )
                )
        }
    }

    private fun setTodoItemGreyCheckbox() {
        itemBinding.apply {
            checkbox.isChecked = false
            checkbox.buttonTintList =
                ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf()),
                    intArrayOf(
                        root.context.getColorFromAttr(android.R.attr.textColorTertiary),
                        root.context.getColorFromAttr(android.R.attr.textColorTertiary)
                    )
                )
        }
    }


    private fun setOnClickListeners(
        todoItem: TodoItem, onItemClickListener: ((TodoItem) -> Unit)?,
        onCheckboxClickListener: ((TodoItem, Boolean) -> Unit)?
    ) {
        itemBinding.apply {
            titleItem
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