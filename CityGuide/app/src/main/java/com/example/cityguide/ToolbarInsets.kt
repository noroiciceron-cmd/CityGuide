package com.example.cityguide

import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Toolbar.applyStatusBarInsets() {
    val initialHeight = layoutParams.height
    val initialPaddingTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        view.setPadding(
            view.paddingLeft,
            initialPaddingTop + statusBarHeight,
            view.paddingRight,
            view.paddingBottom
        )
        view.layoutParams = view.layoutParams.apply {
            height = initialHeight + statusBarHeight
        }
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
