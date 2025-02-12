package com.evg.chart.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.evg.TrackTile
import com.evg.TrackTileShimmer
import com.evg.TracksNotFound
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.model.TrackData
import com.evg.resource.R
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.snackbar.SnackBarEvent
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.LazyColumnSpacedBy
import com.evg.ui.theme.VerticalPadding
import kotlinx.coroutines.flow.flowOf

@Composable
fun SearchLazyColumn(
    isChartLoading: Boolean,
    foundedTracks: LazyPagingItems<ServerResult<TrackData, NetworkError>>,
    onClick: (id: Long) -> Unit,
) {
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
                TracksNotFound()
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
                            val trt = item.error
                        }
                        null -> {}
                    }
                }
            }
        }

        is LoadState.Error -> {
            TracksNotFound()
            val errorMessage = stringResource(id = R.string.server_error)
            LaunchedEffect(errorMessage) {
                SnackBarController.sendEvent(SnackBarEvent(errorMessage))
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
                        listOf<ServerResult<TrackData, NetworkError>>(
                            ServerResult.Success(
                                TrackData(
                                    trackID = 0,
                                    trackTitle = "trackTitle",
                                    trackPreview = "trackPreview",
                                    artistName = "artistName",
                                    albumID = 1,
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
            )
        }
    }
}