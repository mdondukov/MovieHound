package com.example.moviehound.ui.itemdecorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MovieItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val column = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                bottom = space
            }

            when (column) {
                0 -> {
                    top = space
                    left = space
                    right = space
                }
                1 -> {
                    top = space
                    right = space
                }
            }
        }
    }
}