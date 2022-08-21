package com.example.todoapp.view

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.todoapp.R
import com.google.android.material.snackbar.ContentViewCallback

class CustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private val animatedProgressBar: ImageView

    init {
        View.inflate(context, R.layout.view_snackbar, this)
        clipToPadding = false
        this.animatedProgressBar = findViewById(R.id.animatedProgressBar)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val animatedNumbers = animatedProgressBar.drawable as AnimatedVectorDrawable
        val animatedProgressBar = animatedProgressBar.background as CustomProgressBar
        animatedNumbers.reset()
        animatedNumbers.start()
        animatedProgressBar.setColor(context.getColorFromAttr(android.R.attr.textColorTertiary))
        animatedProgressBar.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        val animatedProgressBar = animatedProgressBar.background as CustomProgressBar
        animatedProgressBar.reset()
    }
}