package com.example.todoapp.view

import android.graphics.Canvas
import android.view.View
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.view.viewmodels.TodoViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ItemTouchHelperCallback @AssistedInject constructor(
    @Assisted("TodoViewModel") private val todoViewModel: TodoViewModel,
    @Assisted("view") private val view: View,
    @Assisted("customSnackbar") private val customSnackbar: CustomSnackbar,
    private val todoAdapter: TodoAdapter
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
) {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("TodoViewModel") todoViewModel: TodoViewModel,
            @Assisted("view") view: View,
            @Assisted("customSnackbar") customSnackbar: CustomSnackbar
        ): ItemTouchHelperCallback
    }

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
        val todoItem = todoAdapter.differ.currentList[position]

        when (rightOrLeft) {
            true -> {
                val checkbox = viewHolder.itemView.findViewById<CheckBox>(R.id.checkbox)
                val newTodoItem = todoItem.copy(done = !checkbox.isChecked)
                todoViewModel.editTodoItem(newTodoItem)
            }
            false -> {
                todoViewModel.deleteTodoItem(todoItem)

                customSnackbar.makeAndShow(view) {
                    todoViewModel.addTodoItem(todoItem)
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
