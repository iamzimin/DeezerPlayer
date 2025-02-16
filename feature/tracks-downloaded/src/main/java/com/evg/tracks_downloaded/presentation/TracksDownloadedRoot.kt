package com.evg.tracks_downloaded.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.evg.tracks_downloaded.presentation.mvi.TracksDownloadedViewModel
import org.orbitmvi.orbit.compose.collectAsState

/**
 * Корневой экран загруженных треков
 *
 * @param viewModel ViewModel загруженных треков
 * @param modifier Модификатор для стилизации
 * @param onPlayerScreen Колбек для перехода на экран плеера
 */
@Composable
fun TracksDownloadedRoot(
    viewModel: TracksDownloadedViewModel = hiltViewModel<TracksDownloadedViewModel>(),
    modifier: Modifier,
    onPlayerScreen: (id: Long) -> Unit,
) {

    TracksDownloadedScreen(
        state = viewModel.collectAsState().value,
        dispatch = viewModel::dispatch,
        modifier = modifier,
        onPlayerScreen = onPlayerScreen,
    )
}