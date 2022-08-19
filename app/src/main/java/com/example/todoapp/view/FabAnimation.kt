package com.example.todoapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabAnimation(
    private val fab: FloatingActionButton,
    private val startColor: Int,
    private val endColor: Int
) {
    private var animatorSet: AnimatorSet? = null
    private val objectAnimators = mutableListOf<ObjectAnimator>()

    init {
        objectAnimators.clear()

        objectAnimators.add(ObjectAnimator.ofFloat(fab, View.ROTATION, -10f, 10f).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })

        objectAnimators.add(ObjectAnimator.ofFloat(fab, View.SCALE_X, 1f, 1.15f).apply {
            duration = 750
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })

        objectAnimators.add(ObjectAnimator.ofFloat(fab, View.SCALE_Y, 1f, 1.15f).apply {
            duration = 750
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })

        objectAnimators.add(ObjectAnimator.ofArgb(
            fab.contentBackground,
            "tint",
            startColor,
            endColor
        ).apply {
            duration = 750
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })
    }

    fun startAnimation() {
        animatorSet = AnimatorSet()
        animatorSet?.playTogether(objectAnimators.toList())
        animatorSet?.interpolator = AccelerateDecelerateInterpolator()
        animatorSet?.start()
    }

    fun endAnimation() {
        animatorSet?.removeAllListeners()
        animatorSet?.end()
        animatorSet?.cancel()
        animatorSet = null
        fab.rotation = 0f
        fab.scaleX = 1f
        fab.scaleY = 1f
        fab.contentBackground?.setTint(startColor)
    }
}