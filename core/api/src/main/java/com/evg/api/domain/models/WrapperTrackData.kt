package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class WrapperTrackData(
    @SerializedName("data") val data: List<TrackResponse>
)