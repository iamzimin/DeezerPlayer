package com.evg.chart.domain.model

data class TrackData(
    val trackID: Long,
    val trackTitle: String,
    val trackPreview: String,
    val artistName: String,
    val albumID: Long,
    val albumCover: String,
)
