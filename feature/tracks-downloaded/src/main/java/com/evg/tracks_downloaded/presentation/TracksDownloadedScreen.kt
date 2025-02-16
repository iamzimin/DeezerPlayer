package com.evg.tracks_downloaded.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evg.tracks_downloaded.presentation.mvi.TracksDownloadedAction
import com.evg.tracks_downloaded.presentation.mvi.TracksDownloadedState
import com.evg.ui.SearchTextField
import com.evg.ui.TracksLazyColumn
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import com.evg.ui.theme.VerticalPadding

/**
 * Экран загруженных треков
 *
 * @param state Текущее состояние экрана
 * @param dispatch Функция для отправки действий
 * @param modifier Модификатор для стилизации
 * @param onPlayerScreen Колбек для перехода на экран плеера
 */
@Composable
fun TracksDownloadedScreen(
    state: TracksDownloadedState,
    dispatch: (action: TracksDownloadedAction) -> Unit,
    modifier: Modifier = Modifier,
    onPlayerScreen: (id: Long) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = HorizontalPadding,
                vertical = VerticalPadding,
            ),
    ) {
        SearchTextField(
            onTextChangeDebounced = { text ->
                dispatch(TracksDownloadedAction.FilterTracksOnScreen(query = text))
            }
        )

        Spacer(modifier = Modifier.height(VerticalPadding))

        TracksLazyColumn(
            isSwipeAvailable = false,
            isTracksLoading = state.isTracksLoading,
            tracks = state.tracksDownloaded,
            onClick = onPlayerScreen,
        )
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