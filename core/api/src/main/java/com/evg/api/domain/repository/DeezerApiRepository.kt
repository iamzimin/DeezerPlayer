package com.evg.api.domain.repository

import com.evg.api.domain.models.ChartResponse
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult

interface DeezerApiRepository {
    suspend fun getChart(): ServerResult<ChartResponse, NetworkError>
}