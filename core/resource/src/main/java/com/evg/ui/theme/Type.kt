package com.evg.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class AppTypography(
    val heading: TextStyle,
    val body: TextStyle,
    val small: TextStyle,
)

enum class AppSize {
    Medium,
}


val mediumTextSize = AppTypography(
    heading = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Normal,
    ),
    body = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    small = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
    ),
)