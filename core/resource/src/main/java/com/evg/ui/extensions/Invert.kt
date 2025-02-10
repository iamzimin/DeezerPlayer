package com.evg.ui.extensions

import androidx.compose.ui.graphics.Color

fun Color.invert(): Color {
    return Color(
        red = 1f - red,
        green = 1f - green,
        blue = 1f - blue,
        alpha = alpha,
    )
}