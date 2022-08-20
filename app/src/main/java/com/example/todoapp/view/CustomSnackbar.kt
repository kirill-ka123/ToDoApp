package com.example.todoapp.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.example.todoapp.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class CustomSnackbar(private val customSnackbarView: View, private val duration: Int) {

    private var snackbarAnimation: AnimatorSet? = null
    fun makeAndShow(
        view: View,
        listener: View.OnClickListener
    ) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE)
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        snackbar.duration = duration

        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.addView(customSnackbarView)

        val progressBarAnimation = getProgressBarAnimation(customSnackbarView)
        val progressTextAnimation = getProgressTextAnimation(customSnackbarView)
        snackbarAnimation = getSnackbarAnimation(progressBarAnimation, progressTextAnimation)

        val button = customSnackbarView.findViewById<TextView>(R.id.tvCancel)
        button.setOnClickListener {
            listener.onClick(it)
            snackbarLayout.removeAllViews()
            snackbar.dismiss()

            snackbarAnimation?.removeAllListeners()
            snackbarAnimation?.end()
            snackbarAnimation?.cancel()
            snackbarAnimation = null
        }

        snackbarAnimation?.start()
        snackbar.show()
    }

    private fun getProgressBarAnimation(customSnackbarView: View): ObjectAnimator? {
        val progressBar =
            customSnackbarView.findViewById<CircularProgressIndicator>(R.id.progressBar)

        val progressBarAnimation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        progressBarAnimation.duration = duration.toLong()

        return progressBarAnimation
    }

    private fun getProgressTextAnimation(customSnackbarView: View): ValueAnimator? {
        val progressText =
            customSnackbarView.findViewById<TextView>(R.id.progressText)

        val array = intArrayOf(1, 2, 3, 4, 5)
        val progressTextAnimation = ValueAnimator.ofInt(*array)
        progressTextAnimation.addUpdateListener { animation ->
            progressText.text = animation.animatedValue.toString()
        }
        progressTextAnimation.duration = duration.toLong()

        return progressTextAnimation
    }

    private fun getSnackbarAnimation(
        progressBarAnimation: ObjectAnimator?,
        progressTextAnimation: ValueAnimator?
    ): AnimatorSet {
        val snackbarAnimation = AnimatorSet()
        snackbarAnimation.interpolator = LinearInterpolator()
        snackbarAnimation.playTogether(progressBarAnimation, progressTextAnimation)

        return snackbarAnimation
    }
}