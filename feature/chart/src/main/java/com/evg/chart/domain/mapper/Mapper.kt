package com.evg.chart.domain.mapper

import com.evg.api.domain.models.ChartResponse
import com.evg.chart.domain.model.ChartData

fun ChartResponse.toChartData(): List<ChartData> {
    return this.tracks.data.map {
        ChartData(
            trackID = it.id,
            trackTitle = it.title,
            trackPreview = it.preview,
            artistName = it.artist.name,
            albumID = it.album.id,
            albumCover = it.album.coverSmall,
        )
    }
}