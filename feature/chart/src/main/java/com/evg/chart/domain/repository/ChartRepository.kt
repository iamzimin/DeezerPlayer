package com.evg.chart.domain.repository

import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.model.ChartData

interface ChartRepository {
    suspend fun getChartTracks(): ServerResult<List<ChartData>, NetworkError>
}