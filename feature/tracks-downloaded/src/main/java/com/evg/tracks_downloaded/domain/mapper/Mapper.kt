package com.evg.tracks_downloaded.domain.mapper

import com.evg.database.domain.models.TracksDBO
import com.evg.tracks_downloaded.domain.model.TrackData

fun TracksDBO.toTrackData(): TrackData {
    return TrackData(
        trackID = this.trackId,
        trackTitle = this.trackTitle,
        artistName = this.artistName,
        albumCover = this.albumCover,
    )
}