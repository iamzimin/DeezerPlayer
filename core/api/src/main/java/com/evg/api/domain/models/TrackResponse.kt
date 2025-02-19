package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("preview") val preview: String,
    @SerialName("artist") val artist: ArtistResponse,
    @SerialName("album") val album: AlbumResponse,
)
