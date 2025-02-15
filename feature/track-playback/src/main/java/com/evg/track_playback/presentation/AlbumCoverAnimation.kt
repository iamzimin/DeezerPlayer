package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


@OptIn(FlowPreview::class)
@Composable
fun AlbumCoverAnimation(
    modifier: Modifier = Modifier,
    isSongPlaying: Boolean,
    albumCover: String,
) {
    var debouncedSongPlaying by remember { mutableStateOf(isSongPlaying) }
    LaunchedEffect(isSongPlaying) {
        snapshotFlow { isSongPlaying }
            .debounce(100)
            .collect { value ->
                debouncedSongPlaying = value
            }
    }

    val scale by animateFloatAsState(
        targetValue = if (debouncedSongPlaying) 1.2f else 1f,
        animationSpec = tween(durationMillis = 500)
    )

    AlbumCover(
        modifier = modifier,
        scale = scale,
        albumCover = albumCover,
        isSongPlaying = debouncedSongPlaying,
    )
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AlbumCoverAnimationPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            AlbumCoverAnimation(
                modifier = Modifier,
                isSongPlaying = true,
                albumCover = "",
            )
        }
    }
}