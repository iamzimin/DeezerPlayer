package com.evg.chart.presentation

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.evg.SearchTextField
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
    var isSearchMode by rememberSaveable { mutableStateOf(false) }
    var searchText by rememberSaveable { mutableStateOf("") }

    val chartTracks = state.chartTracks
    val foundedTracks = state.foundedTracks.collectAsLazyPagingItems()

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
                if (text.isNotBlank()) {
                    dispatch(ChartAction.SearchTrack(query = text))
                }
            }
        )

        Spacer(modifier = Modifier.height(VerticalPadding))

        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize(),
            state = refreshingState,
            swipeEnabled = !state.isChartLoading && foundedTracks.loadState.refresh !is LoadState.Loading,
            onRefresh = {
                if (isSearchMode) {
                    dispatch(ChartAction.SearchTrack(query = searchText))
                } else {
                    dispatch(ChartAction.GetChart)
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
            if (isSearchMode) {
                SearchLazyColumn(
                    isChartLoading = state.isChartLoading,
                    foundedTracks = foundedTracks,
                    onClick = onPlayerScreen,
                )
            } else {
                ChartLazyColumn(
                    isChartLoading = state.isChartLoading,
                    chartTracks = chartTracks,
                    onClick = onPlayerScreen,
                )
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