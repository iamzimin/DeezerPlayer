package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class ArtistResponse(
    @SerializedName("name") val name: String,
)
