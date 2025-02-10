package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("preview") val preview: String,
    @SerializedName("artist") val artist: ArtistResponse,
    @SerializedName("album") val album: AlbumResponse,
)
