package com.evg.chart.presentation.mvi

import androidx.paging.PagingData
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.model.TrackData
import kotlinx.coroutines.flow.MutableStateFlow

data class ChartState(
    val isChartLoading: Boolean = false,
    val chartTracks: List<TrackData> = emptyList(),
    val foundedTracks: MutableStateFlow<PagingData<ServerResult<TrackData, NetworkError>>> = MutableStateFlow(PagingData.empty())
)