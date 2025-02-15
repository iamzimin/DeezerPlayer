package com.evg.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.evg.ui.theme.AppTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun Shimmer(
    modifier: Modifier,
) {
    Box(
        modifier = Modifier.shimmer(),
    ) {
        Box(
            modifier = modifier
                .background(AppTheme.colors.shimmer)
        )
    }
}