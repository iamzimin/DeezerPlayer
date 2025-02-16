package com.evg.ui.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.evg.ui.theme.AppTheme

/**
 * Добавляет эффект ряби при клике
 *
 * @param color Цвет ряби (по умолчанию используется цвет текста из темы)
 * @param onClick Действие, выполняемое при клике
 * @return [Modifier] с эффектом ряби
 */
fun Modifier.clickableRipple(color: Color? = null, onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = color ?: AppTheme.colors.text, bounded = false),
    ) {
        onClick()
    }
}