package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class ChartResponse(
    @SerializedName("tracks") val tracks: WrapperTrackData
)
