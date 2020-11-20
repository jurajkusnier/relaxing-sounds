package com.jurajkusnier.natureandrelaxingsounds.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val spacePx: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            left = spacePx
            top = spacePx
            bottom = spacePx
            right = spacePx
        }
    }
}