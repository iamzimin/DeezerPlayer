package com.evg.chart.data.repository

import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.api.domain.utils.mapData
import com.evg.chart.domain.mapper.toChartData
import com.evg.chart.domain.model.ChartData
import com.evg.chart.domain.repository.ChartRepository

class ChartRepositoryImpl(
    private val apiRepository: DeezerApiRepository,
): ChartRepository {
    override suspend fun getChartTracks(): ServerResult<List<ChartData>, NetworkError> {
        return apiRepository.getChart().mapData {
            it.toChartData()
        }
    }
}