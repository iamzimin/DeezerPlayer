package com.evg.deezerplayer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.evg.ui.theme.AppTheme

/**
 * Scaffold добавляющий отступы
 *
 * @param modifier Модификатор для кастомизации
 * @param isContainerTransparent Флаг, указывающий, является фон прозрачным
 * @param content Контент экрана с отступами
 */
@Composable
fun DeezerPlayerScaffold(
    modifier: Modifier = Modifier,
    isContainerTransparent: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isContainerTransparent) Color.Transparent else AppTheme.colors.background,
    ) { paddingValues ->
        content(paddingValues)
    }
}