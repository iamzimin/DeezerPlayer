package com.evg.deezerplayer.snackbar

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.evg.ui.theme.AppTheme

/**
 * Определяет направление свайпа
 */
enum class SwipeDirection {
    Left,
    Initial,
    Right,
}

/**
 * Отображает хост для всплывающих уведомлений с поддержкой свайпа
 *
 * @param hostState Состояние хоста всплывающих уведомлений
 */
@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableSnackBarHost(hostState: SnackbarHostState) {
    if (hostState.currentSnackbarData == null) { return }
    var size by remember { mutableStateOf(Size.Zero) }
    val swipeableState = rememberSwipeableState(SwipeDirection.Initial)
    val width = remember(size) {
        if (size.width == 0f) {
            1f
        } else {
            size.width
        }
    }
    if (swipeableState.isAnimationRunning) {
        DisposableEffect(Unit) {
            onDispose {
                when (swipeableState.currentValue) {
                    SwipeDirection.Right,
                    SwipeDirection.Left -> {
                        hostState.currentSnackbarData?.dismiss()
                    }
                    else -> {
                        return@onDispose
                    }
                }
            }
        }
    }
    val offset = with(LocalDensity.current) {
        swipeableState.offset.value.toDp()
    }
    SnackbarHost(
        hostState = hostState,
        snackbar = { snackBarData ->
            Snackbar(
                snackbarData = snackBarData,
                modifier = Modifier.offset(x = offset),
                containerColor = AppTheme.colors.snackBarBackground,
                contentColor = AppTheme.colors.text,
                actionColor = AppTheme.colors.primary,
            )
        },
        modifier = Modifier
            .onSizeChanged { size = Size(it.width.toFloat(), it.height.toFloat()) }
            .swipeable(
                state = swipeableState,
                anchors = mapOf(
                    -width to SwipeDirection.Left,
                    0f to SwipeDirection.Initial,
                    width to SwipeDirection.Right,
                ),
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal,
            )
    )
}