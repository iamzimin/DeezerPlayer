package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WrapperTrackData(
    @SerialName("data") val data: List<TrackResponse>
)