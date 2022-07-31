package com.example.todoapp.decor

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class RecyclerVIewSwipeDecorator(
    private val canvas: Canvas,
    private val recyclerView: RecyclerView,
    private val viewHolder: RecyclerView.ViewHolder,
    private val dX: Float,
    private val actionState: Int
) {
    private var iconHorizontalMargin = 0

    private var swipeLeftBackgroundColor = 0
    private var swipeLeftActionIconId = 0
    private var swipeLeftActionIconTint = 0

    private var swipeRightBackgroundColor = 0
    private var swipeRightActionIconId = 0
    private var swipeRightActionIconTint = 0

    fun decorate() {
        try {
            if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return
            if (dX > 0) {
                // Swiping Right
                canvas.clipRect(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top,
                    viewHolder.itemView.left + dX.toInt(),
                    viewHolder.itemView.bottom
                )
                if (swipeRightBackgroundColor != 0) {
                    val background = GradientDrawable()
                    background.setColor(swipeRightBackgroundColor)
                    background.setBounds(
                        viewHolder.itemView.left,
                        viewHolder.itemView.top,
                        viewHolder.itemView.left + dX.toInt(),
                        viewHolder.itemView.bottom
                    )
                    background.draw(canvas)
                }
                if (swipeRightActionIconId != 0 && dX > iconHorizontalMargin) {
                    val icon =
                        ContextCompat.getDrawable(recyclerView.context, swipeRightActionIconId)
                    if (icon != null) {
                        val iconSize = icon.intrinsicHeight

                        if (dX < iconSize + 2 * iconHorizontalMargin) {
                            val left = viewHolder.itemView.left + iconHorizontalMargin
                            val top: Int = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - iconSize / 2)
                            val right = viewHolder.itemView.left + iconHorizontalMargin + iconSize
                            val bottom = top + iconSize
                            icon.setBounds(left, top, right, bottom)
                        } else {
                            val left = viewHolder.itemView.left - iconSize - iconHorizontalMargin + dX.toInt()
                            val top: Int = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - iconSize / 2)
                            val right = viewHolder.itemView.left - iconHorizontalMargin + dX.toInt()
                            val bottom = top + iconSize
                            icon.setBounds(left, top, right, bottom)
                        }

                        if (swipeRightActionIconTint != 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                icon.colorFilter = BlendModeColorFilter(swipeRightActionIconTint, BlendMode.SRC_IN)
                            } else {
                                icon.setColorFilter(swipeRightActionIconTint, PorterDuff.Mode.SRC_IN)
                            }
                        }
                        icon.draw(canvas)
                    }
                }
            } else if (dX < 0) {
                // Swiping Left
                canvas.clipRect(
                    viewHolder.itemView.right + dX.toInt(),
                    viewHolder.itemView.top,
                    viewHolder.itemView.right,
                    viewHolder.itemView.bottom
                )
                if (swipeLeftBackgroundColor != 0) {
                    val background = GradientDrawable()
                    background.setColor(swipeLeftBackgroundColor)
                    background.setBounds(
                        viewHolder.itemView.right + dX.toInt(),
                        viewHolder.itemView.top,
                        viewHolder.itemView.right,
                        viewHolder.itemView.bottom
                    )
                    background.draw(canvas)
                }
                if (swipeLeftActionIconId != 0 && dX < -iconHorizontalMargin) {
                    val icon =
                        ContextCompat.getDrawable(recyclerView.context, swipeLeftActionIconId)
                    if (icon != null) {
                        val iconSize = icon.intrinsicHeight

                        if (dX > -(iconSize + 2 * iconHorizontalMargin)) {
                            val left = viewHolder.itemView.right - iconHorizontalMargin - iconSize
                            val top = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - iconSize / 2)
                            val right = viewHolder.itemView.right - iconHorizontalMargin
                            val bottom = top + icon.intrinsicHeight
                            icon.setBounds(left, top, right, bottom)
                        } else {
                            val left = viewHolder.itemView.right + iconHorizontalMargin + dX.toInt()
                            val top = viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - iconSize / 2)
                            val right = viewHolder.itemView.right + iconHorizontalMargin + iconSize + dX.toInt()
                            val bottom = top + icon.intrinsicHeight
                            icon.setBounds(left, top, right, bottom)
                        }

                        if (swipeLeftActionIconTint != 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                icon.colorFilter = BlendModeColorFilter(swipeLeftActionIconTint, BlendMode.SRC_IN)
                            } else {
                                icon.setColorFilter(swipeLeftActionIconTint, PorterDuff.Mode.SRC_IN)
                            }
                        }
                        icon.draw(canvas)
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { Log.e(this.javaClass.name, it) }
        }
    }

    fun setSwipeLeftBackgroundColor(color: Int) {
        swipeLeftBackgroundColor = color
    }

    fun setSwipeLeftActionIcon(drawableId: Int) {
        swipeLeftActionIconId = drawableId
    }

    fun setSwipeRightBackgroundColor(color: Int) {
        swipeRightBackgroundColor = color
    }

    fun setSwipeRightActionIcon(drawableId: Int) {
        swipeRightActionIconId = drawableId
    }

    fun setSwipeLeftActionIconTint(color: Int) {
        swipeLeftActionIconTint = color
    }

    fun setSwipeRightActionIconTint(color: Int) {
        swipeRightActionIconTint = color
    }

    fun setIconHorizontalMargin(margin: Int) {
        iconHorizontalMargin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            margin.toFloat(),
            recyclerView.context.resources.displayMetrics
        ).toInt()
    }
}