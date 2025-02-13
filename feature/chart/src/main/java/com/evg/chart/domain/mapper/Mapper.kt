package com.evg.chart.domain.mapper

import com.evg.api.domain.models.TrackResponse
import com.evg.chart.domain.model.TrackData

fun TrackResponse.toTrackData(): TrackData {
    return TrackData(
        trackID = this.id,
        trackTitle = this.title,
        artistName = this.artist.name,
        albumCover = this.album.cover,
    )
}