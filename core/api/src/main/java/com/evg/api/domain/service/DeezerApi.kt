package com.evg.api.domain.service

import com.evg.api.domain.models.ChartResponse
import retrofit2.http.GET

interface DeezerApi {
    @GET("chart")
    suspend fun getChart(): ChartResponse
}