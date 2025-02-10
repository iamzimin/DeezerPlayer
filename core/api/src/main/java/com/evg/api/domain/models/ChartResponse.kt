package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class ChartResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("preview") val preview: String,
)
