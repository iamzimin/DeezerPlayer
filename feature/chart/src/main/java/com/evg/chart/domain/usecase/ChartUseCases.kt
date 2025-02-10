package com.evg.chart.domain.usecase

import javax.inject.Inject

data class ChartUseCases @Inject constructor(
    val getChartUseCase: GetChartUseCase,
)