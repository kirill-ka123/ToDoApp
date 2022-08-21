package com.example.todoapp.view

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.SystemClock

class CustomProgressBar : Drawable() {

    private val paint = Paint()

    private var circle = RectF(40f, 40f, 80f, 80f)

    private val duration = PROGRESS_BAR_ANIMATION_DURATION
    private var previousDrawTime = 0L
    private val degreeDelta = duration.toFloat() / 360f

    private var isStarted = false

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun draw(canvas: Canvas) {
        if (isStarted) {
            val now = SystemClock.uptimeMillis()
            val timeDelta = (now - previousDrawTime) % duration

            canvas.drawArc(circle, 270f, timeDelta.toFloat() / degreeDelta - 360f, false, paint)
            invalidateSelf()
        }
    }

    fun start() {
        previousDrawTime = SystemClock.uptimeMillis()
        isStarted = true
    }

    fun reset() {
        isStarted = false
    }

    fun setColor(color: Int) {
        paint.color = color
    }

    fun setSize(size: Float, margin: Float) {
        circle = RectF(margin, margin, margin + size / 2, margin + size / 2)
    }

    companion object {
        const val PROGRESS_BAR_ANIMATION_DURATION = 5000L
    }
}
