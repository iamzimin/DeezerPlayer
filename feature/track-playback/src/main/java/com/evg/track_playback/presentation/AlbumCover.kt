package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.ui.PreviewImage
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.BorderRadius
import com.evg.ui.theme.DeezerPlayerTheme

@Composable
fun AlbumCover(
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    albumCover: String,
    isSongPlaying: Boolean,
) {
    val elevation by animateDpAsState(
        targetValue = if (isSongPlaying) 10.dp else 0.dp,
        animationSpec = tween(500)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .scale(scale)
            .clip(RoundedCornerShape(BorderRadius))
            .shadow(elevation)
    ) {
        PreviewImage(
            modifier = Modifier
                .fillMaxSize(),
            albumCover = albumCover,
        )
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun AlbumCoverPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            AlbumCover(
                modifier = Modifier,
                isSongPlaying = true,
                albumCover = "",
            )
        }
    }
}