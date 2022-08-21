package com.example.todoapp.view

import android.content.Context

fun Context.convertDpToPixels(dp: Float) =
    dp * this.resources.displayMetrics.density