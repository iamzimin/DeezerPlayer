package com.evg.api.domain.service

import com.evg.api.domain.models.ChartResponse
import com.evg.api.domain.models.SearchTrackResponse
import com.evg.api.domain.models.TrackResponse
import com.evg.api.domain.models.WrappedAlbumData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {
    @GET("chart")
    suspend fun getChart(): ChartResponse

    @GET("search")
    suspend fun searchTrack(
        @Query("q") query: String,
        @Query("index") index: Int = 0
    ): SearchTrackResponse

    @GET("track/{id}")
    suspend fun getTrackById(
        @Path("id") id: Long,
    ): TrackResponse

    @GET("album/{id}/tracks")
    suspend fun getAlbumById(
        @Path("id") id: Long,
    ): WrappedAlbumData
}