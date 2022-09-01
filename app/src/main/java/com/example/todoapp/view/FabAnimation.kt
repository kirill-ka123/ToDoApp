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
    private var fabAnimation: AnimatorSet? = null
    private val objectAnimators = mutableListOf<ObjectAnimator>()

    init {
        objectAnimators.add(ObjectAnimator.ofFloat(fab, View.ROTATION, -10f, 10f).apply {
            duration = FAB_ANIMATION_DURATION
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })

        objectAnimators.add(ObjectAnimator.ofFloat(fab, View.SCALE_X, 1f, 1.15f).apply {
            duration = FAB_ANIMATION_DURATION / 2
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })

        objectAnimators.add(ObjectAnimator.ofFloat(fab, View.SCALE_Y, 1f, 1.15f).apply {
            duration = FAB_ANIMATION_DURATION / 2
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })

        objectAnimators.add(ObjectAnimator.ofArgb(
            fab.contentBackground,
            "tint",
            startColor,
            endColor
        ).apply {
            duration = FAB_ANIMATION_DURATION / 2
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        })
    }

    fun startAnimation() {
        fabAnimation = AnimatorSet()
        fabAnimation?.playTogether(objectAnimators.toList())
        fabAnimation?.interpolator = AccelerateDecelerateInterpolator()
        fabAnimation?.start()
    }

    fun endAnimation() {
        fabAnimation?.removeAllListeners()
        fabAnimation?.end()
        fabAnimation?.cancel()
        fabAnimation = null
        fab.rotation = 0f
        fab.scaleX = 1f
        fab.scaleY = 1f
        fab.contentBackground?.setTint(startColor)
    }

    companion object {
        const val FAB_ANIMATION_DURATION = 1500L
    }
}