package com.evg.chart.presentation.mvi

sealed class ChartSideEffect {
    data object ChartLoadFail: ChartSideEffect()
}