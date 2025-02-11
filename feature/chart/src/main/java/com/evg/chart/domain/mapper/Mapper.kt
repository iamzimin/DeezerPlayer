package com.evg.chart.domain.mapper

import com.evg.api.domain.models.TrackResponse
import com.evg.chart.domain.model.TrackData

fun TrackResponse.toChartData(): TrackData {
    return TrackData(
        trackID = this.id,
        trackTitle = this.title,
        trackPreview = this.preview,
        artistName = this.artist.name,
        albumID = this.album.id,
        albumCover = this.album.cover,
    )
}