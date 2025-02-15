package com.evg.deezerplayer.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.evg.ui.theme.AppTheme

@Composable
fun DeezerPlayerScaffold(
    modifier: Modifier = Modifier,
    isContainerTransient: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = if (isContainerTransient) Color.Transparent else AppTheme.colors.background,
    ) { paddingValues ->
        content(paddingValues)
    }
}