package com.evg.chart.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.TrackTile
import com.evg.chart.presentation.mvi.ChartAction
import com.evg.chart.presentation.mvi.ChartState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import com.evg.ui.theme.VerticalPadding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun ChartScreen(
    state: ChartState,
    dispatch: (action: ChartAction) -> Unit,
    modifier: Modifier = Modifier,
    onPlayerScreen: (id: Long) -> Unit,
) {
    val refreshingState = rememberSwipeRefreshState(isRefreshing = false)

    val chartTracks = state.chartTracks

    val spacedByPadding = 15.dp

    Column(
        modifier = modifier
            .padding(
                horizontal = HorizontalPadding,
                vertical = VerticalPadding,
            ),
    ) {
        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize(),
            state = refreshingState,
            swipeEnabled = !state.isChartLoading,
            onRefresh = { dispatch(ChartAction.GetChart) },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    backgroundColor = AppTheme.colors.background,
                    contentColor = AppTheme.colors.primary,
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(spacedByPadding)
            ) {
                items(
                    count = chartTracks.size,
                ) {
                    val chartTrackData = chartTracks.getOrNull(it)
                    chartTrackData?.let { data ->
                        TrackTile(
                            albumCover = data.albumCover,
                            trackTitle = data.trackTitle,
                            artistName = data.artistName,
                            onClick = {
                                //onPlayerScreen(data.trackID)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ChartScreenPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            ChartScreen(
                state = ChartState(
                    isChartLoading = false,
                ),
                dispatch = {},
                onPlayerScreen = {},
            )
        }
    }
}