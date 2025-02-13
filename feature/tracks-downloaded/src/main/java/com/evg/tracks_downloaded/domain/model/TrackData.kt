package com.evg.tracks_downloaded.domain.model

data class TrackData(
    val trackID: Long,
    val trackTitle: String,
    val artistName: String,
    val albumCover: String,
)
