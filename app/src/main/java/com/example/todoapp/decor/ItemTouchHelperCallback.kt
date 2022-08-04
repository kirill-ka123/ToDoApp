package com.example.todoapp.decor

import android.graphics.Canvas
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.ui.adapters.CasesAdapter
import com.example.todoapp.ui.viewModels.todoViewModel.TodoViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.todo_item.view.*

class ItemTouchHelperCallback(
    private val todoViewModel: TodoViewModel?,
    private val casesAdapter: CasesAdapter,
    private val view: View
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
) {
    private var rightOrLeft: Boolean? = null
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val todoItem = casesAdapter.differ.currentList[position]

        when (rightOrLeft) {
            true -> {
                val newTodoItem = todoItem.copy(done = !viewHolder.itemView.checkbox.isChecked)
                todoViewModel?.putTodoItemNetwork(newTodoItem)
            }
            false -> {
                todoViewModel?.deleteTodoItemNetwork(todoItem)
                Snackbar.make(
                    view,
                    view.resources.getString(R.string.case_was_deleted),
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(view.resources.getString(R.string.cancel)) {
                        todoViewModel?.postTodoItemNetwork(todoItem, todoItem.id)
                    }
                    show()
                }
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        rightOrLeft = dX > 0
        RecyclerVIewSwipeDecorator(c, recyclerView, viewHolder, dX, actionState).apply {
            setSwipeRightBackgroundColor(ContextCompat.getColor(view.context, R.color.green))
            setSwipeRightActionIcon(R.drawable.ic_check)
            setSwipeRightActionIconTint(ContextCompat.getColor(view.context, R.color.white))

            setSwipeLeftBackgroundColor(ContextCompat.getColor(view.context, R.color.red))
            setSwipeLeftActionIcon(R.drawable.ic_delete)
            setSwipeLeftActionIconTint(ContextCompat.getColor(view.context, R.color.white))

            setIconHorizontalMargin(27)

            decorate()
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
