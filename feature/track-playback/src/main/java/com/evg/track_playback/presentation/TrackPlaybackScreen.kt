package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.evg.resource.R
import com.evg.track_playback.presentation.model.PlaylistState
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.track_playback.presentation.mvi.TrackPlaybackState
import com.evg.ui.NotFound
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.VerticalPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * Основной экран воспроизведения трека
 *
 * @param state Текущее состояние воспроизведения
 * @param dispatch Функция для отправки действий воспроизведения
 * @param modifier Модификатор для стилизации
 * @param onPreviousScreen Функция для возврата на предыдущий экран
 * @param onBackgroundImageReady Функция отправки в root url изображения
 */
@Composable
fun TrackPlaybackScreen(
    state: TrackPlaybackState,
    dispatch: (TrackPlaybackAction) -> Unit,
    modifier: Modifier = Modifier,
    onPreviousScreen: () -> Unit,
    onBackgroundImageReady: (url: String) -> Unit,
) {
    val uiState = state.playlistState
    val refreshingState = rememberSwipeRefreshState(isRefreshing = false)

    Column(
        modifier = modifier
            .padding(
                vertical = VerticalPadding,
            )
    ) {
        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize(),
            state = refreshingState,
            swipeEnabled = uiState is PlaylistState.Error,
            onRefresh = { dispatch(TrackPlaybackAction.LoadPlaylist) },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    backgroundColor = AppTheme.colors.background,
                    contentColor = AppTheme.colors.primary,
                )
            },
        ) {
            when (uiState) {
                PlaylistState.Loading -> {
                    SongScreenShimmer()
                }
                PlaylistState.Error -> {
                    NotFound(
                        displayText = buildString {
                            append(stringResource(id = R.string.track_loading_error))
                            append("\n")
                            append(stringResource(id = R.string.swipe_to_update))
                        }
                    )
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