package com.evg.api.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtistResponse(
    @SerialName("name") val name: String,
)
