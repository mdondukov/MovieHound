package com.example.moviehound.util

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.internal.ViewUtils.requestApplyInsetsWhenAttached

fun View.doOnApplyWindowInsets(block: (View, insets: WindowInsetsCompat, initialMargin: Rect) -> WindowInsetsCompat) {
    val initialMargin = recordInitialMarginForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        block(v, insets, initialMargin)
    }
    requestApplyInsetsWhenAttached(this)
}

private fun recordInitialMarginForView(view: View): Rect {
    val params = view.layoutParams as ViewGroup.MarginLayoutParams
    return Rect(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin)
}