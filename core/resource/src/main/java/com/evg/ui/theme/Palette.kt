package com.evg.ui.theme

import androidx.compose.ui.graphics.Color

data class AppPalette(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val shimmer: Color,

    val text: Color,

    // TextField
    val textField: Color,
    val textFieldName: Color,
    val textFieldBackground: Color,

    // BottomBar
    val bottomBarSelected: Color,
)

enum class AppStyle {
    Green,
}

val baseDarkPalette = AppPalette(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF2E7D32),
    background = Color(0xFF1B1F1B),
    shimmer = Color.LightGray,

    text = Color(0xFFFFFFFF),

    // TextField
    textField = Color(0xFF6C8D6C),
    textFieldName = Color(0xFFAAAAAA),
    textFieldBackground = Color(0xFF1C2D1C),

    // BottomBar
    bottomBarSelected = Color(0xFF2F5234),
)

val baseLightPalette = AppPalette(
    primary = Color(0xFF4CAF50),
    secondary = Color(0xFF81C784),
    background = Color(0xFFFFFFFF),
    shimmer = Color.Gray,

    text = Color(0xFF000000),

    // TextField
    textField = Color(0xFF6C8D6C),
    textFieldName = Color(0xFFAAAAAA),
    textFieldBackground = Color(0xFFE8F5E9),

    // BottomBar
    bottomBarSelected = Color(0xFF388E3C),
)