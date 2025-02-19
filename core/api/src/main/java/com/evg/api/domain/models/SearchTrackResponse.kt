package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchTrackResponse(
    @SerialName("data") val data: List<TrackResponse>,
    @SerialName("prev") val prev: String? = null,
    @SerialName("next") val next: String? = null,
)
