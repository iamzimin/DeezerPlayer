package com.evg.api.domain.models

import com.google.gson.annotations.SerializedName

data class WrappedAlbumData(
    @SerializedName("data") val data: List<AlbumResponse>
)
