package com.evg.track_playback.domain.mapper

import com.evg.api.domain.models.TrackResponse
import com.evg.track_playback.domain.model.TrackData

fun TrackResponse.toTrackData(): TrackData {
    return TrackData(
        trackID = this.id,
        trackTitle = this.title,
        trackPreview = this.preview,
        artistName = this.artist.name,
        albumID = this.album.id,
        albumCover = this.album.cover,
    )
}