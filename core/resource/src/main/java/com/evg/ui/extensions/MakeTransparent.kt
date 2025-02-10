package com.evg.ui.extensions

import androidx.compose.ui.graphics.Color

fun Color.makeTransparent(factor: Float): Color {
    return this.copy(alpha = (this.alpha * (1 - factor)).coerceIn(0f, 1f))
}
