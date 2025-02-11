package com.evg.chart.domain.repository

import androidx.paging.PagingData
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.model.TrackData
import kotlinx.coroutines.flow.Flow

interface ChartRepository {
    suspend fun getChartTracks(): ServerResult<List<TrackData>, NetworkError>
    suspend fun searchTrack(query: String): Flow<PagingData<ServerResult<TrackData, NetworkError>>>
}