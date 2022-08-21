package com.example.todoapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.todoapp.R
import com.google.android.material.snackbar.BaseTransientBottomBar

class CustomSnackbar(
    parent: ViewGroup,
    content: CustomSnackbarView
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {
        fun make(view: View): CustomSnackbar {
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )
            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.layout_snackbar,
                parent,
                false
            ) as CustomSnackbarView

            return CustomSnackbar(parent, customView)
        }

        fun CustomSnackbar.setAction(listener: View.OnClickListener) {
            getView().findViewById<TextView>(R.id.tvCancel).setOnClickListener {
                listener.onClick(it)
                dismiss()
            }
        }
    }
}