package com.example.todoapp.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.common.Utils
import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem
import kotlinx.android.synthetic.main.todo_item.view.*


class CasesAdapter : RecyclerView.Adapter<CasesAdapter.CasesViewHolder>() {
    class CasesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                            setTodoItemHighImportance()
                        }
                        Importance.LOW -> {
                            setTodoItemLowImportance()
                        }
                        Importance.BASIC -> {
                            setTodoItemBasicImportance()
                        }
                    }
                }
                if (todoItem.deadline > 0L) {
                    setTodoItemDeadline(Utils.convertUnixToDate(todoItem.deadline))
                }
                if (checkbox.isChecked) {
                    setTodoItemGreenCheckbox()
                } else {
                    if (todoItem.importance == Importance.IMPORTANT) {
                        setTodoItemRedCheckbox()
                    } else {
                        setTodoItemGreyCheckbox()
                    }
                }
                setOnClickListeners(todoItem, onItemClickListener, onCheckboxClickListener)
            }
        }

        private fun setTodoItemDone() {
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

        private fun setTodoItemDeadline(deadline: String) {
            itemView.apply {
                tvDate.visibility = View.VISIBLE
                tvDate.text = deadline
            }
        }

        private fun setTodoItemGreenCheckbox() {
            itemView.apply {
                checkbox.buttonTintList =
                    ContextCompat.getColorStateList(context, R.color.green)
            }
        }

        private fun setTodoItemRedCheckbox() {
            itemView.apply {
                checkbox.buttonTintList =
                    ContextCompat.getColorStateList(context, R.color.red)
            }
        }

        private fun setTodoItemGreyCheckbox() {
            itemView.apply {
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