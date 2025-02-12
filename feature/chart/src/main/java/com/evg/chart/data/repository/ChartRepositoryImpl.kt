package com.evg.chart.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.evg.api.data.SearchTrackPageSourceRemote
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.api.domain.utils.mapData
import com.evg.chart.domain.mapper.toTrackData
import com.evg.chart.domain.model.TrackData
import com.evg.chart.domain.repository.ChartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChartRepositoryImpl(
    private val apiRepository: DeezerApiRepository,
    private val searchTrackPageSourceRemote: SearchTrackPageSourceRemote,
): ChartRepository {
    override suspend fun getChartTracks(): ServerResult<List<TrackData>, NetworkError> {
        return apiRepository.getChart().mapData { chartResponse ->
            chartResponse.tracks.data.map { trackResponse ->
                trackResponse.toTrackData()
            }
        }
    }

    override suspend fun searchTrack(query: String): Flow<PagingData<ServerResult<TrackData, NetworkError>>> {
        return Pager(
            PagingConfig(
                pageSize = 25,
            )
        ) { searchTrackPageSourceRemote.apply { this.query = query } }
            .flow
            .map { pagingData ->
                pagingData.map { data ->
                    data.mapData {
                        it.toTrackData()
                    }
                }
            }
    }
}