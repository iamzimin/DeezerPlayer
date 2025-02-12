package com.evg.api.domain.repository

import com.evg.api.domain.models.ChartResponse
import com.evg.api.domain.models.SearchTrackResponse
import com.evg.api.domain.models.TrackResponse
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult

interface DeezerApiRepository {
    suspend fun getChart(): ServerResult<ChartResponse, NetworkError>
    suspend fun searchTrackByPage(query: String, index: Int): ServerResult<SearchTrackResponse, NetworkError>
    suspend fun getAlbumByTrackId(id: Long): ServerResult<List<TrackResponse>, NetworkError>
}