package com.evg.track_playback.domain.mapper

import com.evg.api.domain.models.TrackResponse
import com.evg.database.domain.models.TracksDBO
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

fun TracksDBO.toTrackData(): TrackData {
    return TrackData(
        trackID = this.trackId,
        trackTitle = this.trackTitle,
        trackPreview = this.trackPreview,
        artistName = this.artistName,
        albumID = this.albumID,
        albumCover = this.albumCover,
    )
}

fun TrackData.toTracksDBO(): TracksDBO {
    return TracksDBO(
        trackId = this.trackID,
        trackTitle = this.trackTitle,
        trackPreview = this.trackPreview,
        artistName = this.artistName,
        albumID = this.albumID,
        albumCover = this.albumCover,
    )
}