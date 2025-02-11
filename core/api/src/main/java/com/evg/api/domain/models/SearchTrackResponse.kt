package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class SearchTrackResponse(
    @SerializedName("data") val data: List<TrackResponse>?,
    @SerializedName("prev") val prev: String?,
    @SerializedName("next") val next: String?,
)
