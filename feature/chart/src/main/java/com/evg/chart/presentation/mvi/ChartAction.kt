package com.evg.chart.presentation.mvi

sealed class ChartAction {
    data object GetChart: ChartAction()
    data class SearchTrack(val query: String): ChartAction()
}