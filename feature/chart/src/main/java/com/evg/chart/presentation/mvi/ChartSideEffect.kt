package com.evg.chart.presentation.mvi

import com.evg.api.domain.utils.NetworkError

sealed class ChartSideEffect {
    data class ChartLoadFail(val error: NetworkError): ChartSideEffect()
}