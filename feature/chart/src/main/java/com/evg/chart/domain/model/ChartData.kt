package com.evg.chart.domain.model

data class ChartData(
    val trackID: Long,
    val trackTitle: String,
    val trackPreview: String,
    val artistName: String,
    val albumID: Long,
    val albumCover: String,
)
