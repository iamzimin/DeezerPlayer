package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlbumIdResponse(
    @SerialName("id") val id: Long,
)
