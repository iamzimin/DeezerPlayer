package com.evg.chart.domain.usecase

import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.model.TrackData
import com.evg.chart.domain.repository.ChartRepository
import javax.inject.Inject

class GetChartUseCase @Inject constructor(
    private val chartRepository: ChartRepository,
) {
    suspend fun invoke(): ServerResult<List<TrackData>, NetworkError> {
        return chartRepository.getChartTracks()
    }
}