package com.evg.ui.extensions

import androidx.compose.ui.graphics.Color

/**
 * Делает цвет более прозрачным
 *
 * @param factor Коэффициент прозрачности (0 – без изменений, 1 – полностью прозрачный)
 * @return Новый цвет с измененной прозрачностью
 */
fun Color.makeTransparent(factor: Float): Color {
    return this.copy(alpha = (this.alpha * (1 - factor)).coerceIn(0f, 1f))
}
