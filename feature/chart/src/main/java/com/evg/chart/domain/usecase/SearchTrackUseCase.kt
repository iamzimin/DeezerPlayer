package com.evg.chart.domain.usecase

import androidx.paging.PagingData
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.model.TrackData
import com.evg.chart.domain.repository.ChartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchTrackUseCase @Inject constructor(
    private val chartRepository: ChartRepository,
) {
    suspend fun invoke(query: String): Flow<PagingData<ServerResult<TrackData, NetworkError>>> {
        return chartRepository.searchTrack(query = query)
    }
}