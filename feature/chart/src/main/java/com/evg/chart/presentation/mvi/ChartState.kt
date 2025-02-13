package com.evg.chart.presentation.mvi

import androidx.paging.PagingData
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.ui.model.TrackTileContent
import kotlinx.coroutines.flow.MutableStateFlow

data class ChartState(
    val isChartLoading: Boolean = true,
    val chartTracks: List<TrackTileContent> = emptyList(),
    val foundedTracks: MutableStateFlow<PagingData<ServerResult<TrackTileContent, NetworkError>>> = MutableStateFlow(PagingData.empty())
)