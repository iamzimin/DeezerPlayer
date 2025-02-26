package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WrappedAlbumData(
    @SerialName("data") val data: List<AlbumIdResponse>
)
