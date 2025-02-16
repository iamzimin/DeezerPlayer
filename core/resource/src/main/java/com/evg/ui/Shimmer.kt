package com.evg.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.evg.ui.theme.AppTheme
import com.valentinilk.shimmer.shimmer

/**
 * Компонент для отображения эффекта анимации загрузки
 *
 * @param modifier Модификатор для основного блока шиммера
 * @param modifierTop Модификатор для контейнера с эффектом шиммера
 */
@Composable
fun Shimmer(
    modifier: Modifier,
    modifierTop: Modifier = Modifier,
) {
    Box(
        modifier = modifierTop.shimmer(),
    ) {
        Box(
            modifier = modifier
                .background(AppTheme.colors.shimmer)
        )
    }
}