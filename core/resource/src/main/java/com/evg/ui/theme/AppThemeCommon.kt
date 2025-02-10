package com.evg.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

object AppTheme {
    val colors: AppPalette
        @Composable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        get() = LocalAppTypography.current
}


val LocalAppColors = staticCompositionLocalOf<AppPalette> {
    error("Colors composition error")
}

val LocalAppTypography = staticCompositionLocalOf<AppTypography> {
    error("Typography composition error")
}
