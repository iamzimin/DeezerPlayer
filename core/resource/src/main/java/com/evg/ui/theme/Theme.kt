package com.evg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Тема оформления Deezer Player
 *
 * @param style Стиль приложения
 * @param textSize Размер текста
 * @param darkTheme Флаг темной темы
 * @param content Контент, на который распространяется тема
 */
@Composable
fun DeezerPlayerTheme(
    style: AppStyle = AppStyle.Green,
    textSize: AppSize = AppSize.Medium,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when (darkTheme) {
        true -> {
            when (style) {
                AppStyle.Green -> baseDarkPalette
            }
        }
        false -> {
            when (style) {
                AppStyle.Green -> baseLightPalette
            }
        }
    }

    val typography = when(textSize) {
        AppSize.Medium -> mediumTextSize
    }

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        content = content,
    )
}