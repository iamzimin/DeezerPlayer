package com.evg.chart.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evg.TrackTile
import com.evg.TrackTileShimmer
import com.evg.TracksNotFound
import com.evg.chart.domain.model.TrackData
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.LazyColumnSpacedBy

@Composable
fun ChartLazyColumn(
    isChartLoading: Boolean,
    chartTracks: List<TrackData>,
    onClick: (id: Long) -> Unit,
) {
    if (!isChartLoading) {
        if (chartTracks.isEmpty()) {
            TracksNotFound()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(LazyColumnSpacedBy)
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
                                onClick(data.trackID)
                            }
                        )
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(LazyColumnSpacedBy)
        ) {
            items(10) {
                TrackTileShimmer()
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ChartLazyColumnPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            ChartLazyColumn(
                isChartLoading = false,
                chartTracks = emptyList(),
                onClick = {},
            )
        }
    }
}