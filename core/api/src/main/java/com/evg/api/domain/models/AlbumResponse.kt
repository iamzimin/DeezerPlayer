package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class AlbumResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("cover_small") val coverSmall: String,
)
