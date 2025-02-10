package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class ChartResponse(
    @SerializedName("tracks") val tracks: TrackDataWrapper
)

data class TrackDataWrapper(
    @SerializedName("data") val data: List<TrackResponse>
)
