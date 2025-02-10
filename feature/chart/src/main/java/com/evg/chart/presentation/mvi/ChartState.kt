package com.evg.chart.presentation.mvi

import com.evg.chart.domain.model.ChartData

data class ChartState(
    val isChartLoading: Boolean = false,
    val chartTracks: List<ChartData> = emptyList(),
)