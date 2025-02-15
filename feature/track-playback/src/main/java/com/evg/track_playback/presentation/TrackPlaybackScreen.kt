package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.evg.track_playback.presentation.model.PlaylistState
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.track_playback.presentation.mvi.TrackPlaybackState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import com.evg.ui.theme.VerticalPadding

@Composable
fun TrackPlaybackScreen(
    state: TrackPlaybackState,
    dispatch: (TrackPlaybackAction) -> Unit,
    modifier: Modifier = Modifier,
    onPreviousScreen: () -> Unit,
    onBackgroundImageReady: (url: String) -> Unit,
) {
    val uiState = state.playlistState

    Column(
        modifier = modifier
            .padding(
                vertical = VerticalPadding,
            )
    ) {

        when (uiState) {
            PlaylistState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppTheme.colors.primary)
                }
            }
            PlaylistState.Error -> {
                Box(Modifier.fillMaxSize().background(Color.Red))
            }
            is PlaylistState.Ready -> {
                SongScreen(
                    dispatch = dispatch,
                    state = uiState,
                    isPlaying = state.isPlaying,
                    progress = state.progress,
                    duration = state.duration,
                    isTrackDownloading = state.isTrackUpdating,
                    onPreviousScreen = onPreviousScreen,
                    onBackgroundImageReady = onBackgroundImageReady,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TrackPlaybackScreenPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            TrackPlaybackScreen(
                state = TrackPlaybackState(
                    playlistState = PlaylistState.Loading,
                ),
                dispatch = {},
                onPreviousScreen = {},
                onBackgroundImageReady = {},
            )
            /*
            TrackData(
                        trackID = 1,
                        trackTitle = "Bad Dreams",
                        trackPreview = "https://cdnt-preview.dzcdn.net/api/1/1/b/4/7/0/b4764070eb914f3885a3bd9bdd497934.mp3",
                        artistName = "Teddy Swims",
                        albumID = 2,
                        albumCover = "https://cdn-images.dzcdn.net/images/cover/ebb148dd7d9d124ea9fbe39d4576fa46/250x250-000000-80-0-0.jpg"
                    )
             */
        }
    }
}