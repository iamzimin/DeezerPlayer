package com.evg.chart.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.evg.ui.TrackTile
import com.evg.ui.TrackTileShimmer
import com.evg.ui.NotFound
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.resource.R
import com.evg.ui.mapper.toErrorMessage
import com.evg.ui.model.TrackTileContent
import com.evg.ui.snackbar.SnackBarAction
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.snackbar.SnackBarEvent
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.LazyColumnSpacedBy
import kotlinx.coroutines.flow.flowOf

/**
 * Отображает список найденных треков с поддержкой пагинации
 *
 * @param isChartLoading Флаг, указывающий, загружаются ли данные
 * @param foundedTracks Список найденных треков с поддержкой пагинации
 * @param onClick Колбэк, вызываемый при выборе трека
 * @param searchTrack Функция для выполнения поиска треков
 */
@Composable
fun SearchLazyColumn(
    isChartLoading: Boolean,
    foundedTracks: LazyPagingItems<ServerResult<TrackTileContent, NetworkError>>,
    onClick: (id: Long) -> Unit,
    searchTrack: () -> Unit,
) {
    val context = LocalContext.current
    val updateString = stringResource(id = R.string.update)

    when (foundedTracks.loadState.refresh) {
        is LoadState.Loading -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(LazyColumnSpacedBy)
            ) {
                items(10) {
                    TrackTileShimmer()
                }
            }
        }

        is LoadState.NotLoading -> {
            if (foundedTracks.itemCount <= 1 && !isChartLoading) {
                NotFound(
                    displayText = buildString {
                        append(stringResource(id = R.string.list_tracks_is_empty))
                        append("\n")
                        append(stringResource(id = R.string.swipe_to_update))
                    }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(LazyColumnSpacedBy)
            ) {
                items(
                    count = foundedTracks.itemCount,
                    key = { it }
                ) { index ->
                    when (val item = foundedTracks[index]) {
                        is ServerResult.Success -> {
                            TrackTile(
                                albumCover = item.data.albumCover,
                                trackTitle = item.data.trackTitle,
                                artistName = item.data.artistName,
                                onClick = {
                                    onClick(item.data.trackID)
                                }
                            )
                        }
                        is ServerResult.Error -> {
                            val errorMessage = item.error.toErrorMessage(context)
                            LaunchedEffect(errorMessage) {
                                SnackBarController.sendEvent(SnackBarEvent(
                                    message = errorMessage,
                                    action = SnackBarAction(
                                        name = updateString,
                                        action = { searchTrack() },
                                    ),
                                ))
                            }
                        }
                        null -> {}
                    }
                }
            }
        }

        is LoadState.Error -> {
            NotFound(
                displayText = buildString {
                    append(stringResource(id = R.string.list_tracks_is_empty))
                    append("\n")
                    append(stringResource(id = R.string.swipe_to_update))
                }
            )
            val errorMessage = stringResource(id = R.string.error_server)
            LaunchedEffect(errorMessage) {
                SnackBarController.sendEvent(SnackBarEvent(
                    message = errorMessage,
                    action = SnackBarAction(
                        name = updateString,
                        action = { searchTrack() },
                    ),
                ))
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SearchLazyColumnPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            SearchLazyColumn(
                isChartLoading = false,
                foundedTracks = flowOf(
                    PagingData.from(
                        listOf<ServerResult<TrackTileContent, NetworkError>>(
                            ServerResult.Success(
                                TrackTileContent(
                                    trackID = 0,
                                    trackTitle = "trackTitle",
                                    artistName = "artistName",
                                    albumCover = "albumCover",
                                )
                            ),
                            /*ServerResult.Error(
                                error = NetworkError.UNKNOWN
                            )*/
                        )
                    )
                ).collectAsLazyPagingItems(),
                onClick = {},
                searchTrack = {},
            )
        }
    }
}