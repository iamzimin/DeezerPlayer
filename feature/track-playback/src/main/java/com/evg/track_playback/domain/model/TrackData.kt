package com.evg.track_playback.domain.model

data class TrackData(
    val trackID: Long,
    val trackTitle: String,
    val trackPreview: String,
    val artistName: String,
    val albumID: Long,
    val albumCover: String,
    val albumTitle: String,
    val isDownloaded: Boolean,
)
