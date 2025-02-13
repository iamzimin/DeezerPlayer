package com.evg.tracks_downloaded.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evg.ui.SearchTextField
import com.evg.ui.TracksLazyColumn
import com.evg.tracks_downloaded.presentation.mvi.TracksDownloadedAction
import com.evg.tracks_downloaded.presentation.mvi.TracksDownloadedState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import com.evg.ui.theme.VerticalPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun TracksDownloadedScreen(
    state: TracksDownloadedState,
    dispatch: (action: TracksDownloadedAction) -> Unit,
    modifier: Modifier = Modifier,
    onPlayerScreen: (id: Long) -> Unit,
) {
    val refreshingState = rememberSwipeRefreshState(isRefreshing = false)
    var isSearchMode by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val tracksDownloaded = state.tracksDownloaded

    Column(
        modifier = modifier
            .padding(
                horizontal = HorizontalPadding,
                vertical = VerticalPadding,
            ),
    ) {
        SearchTextField(
            onTextChangeDebounced = { text ->
                searchText = text
                isSearchMode = text.isNotBlank()
                dispatch(TracksDownloadedAction.FilterTracksOnScreen(query = text))
            }
        )

        Spacer(modifier = Modifier.height(VerticalPadding))

        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize(),
            state = refreshingState,
            swipeEnabled = !state.isTracksLoading,
            onRefresh = {
                if (isSearchMode) {
                    dispatch(TracksDownloadedAction.SearchTracksDownloaded(query = searchText))
                } else {
                    dispatch(TracksDownloadedAction.GetTracksDownloaded)
                }
            },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    backgroundColor = AppTheme.colors.background,
                    contentColor = AppTheme.colors.primary,
                )
            },
        ) {
            TracksLazyColumn(
                isTracksLoading = state.isTracksLoading,
                tracks = tracksDownloaded,
                onClick = onPlayerScreen,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ChartScreenPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            TracksDownloadedScreen(
                state = TracksDownloadedState(
                    isTracksLoading = false,
                ),
                dispatch = {},
                onPlayerScreen = {},
            )
        }
    }
}