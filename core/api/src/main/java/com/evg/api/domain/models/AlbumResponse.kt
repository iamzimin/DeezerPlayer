package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("cover_big") val cover: String,
)
