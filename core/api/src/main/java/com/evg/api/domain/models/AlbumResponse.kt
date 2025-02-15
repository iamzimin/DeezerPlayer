package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class AlbumResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("cover_big") val cover: String,
)
