package com.evg.ui.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

fun Color.lighten(factor: Float): Color {
    return lerp(this, Color.White, factor.coerceIn(0f, 1f))
}