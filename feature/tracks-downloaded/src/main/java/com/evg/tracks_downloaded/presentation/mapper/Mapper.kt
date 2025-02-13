package com.evg.tracks_downloaded.presentation.mapper

import com.evg.tracks_downloaded.domain.model.TrackData
import com.evg.ui.model.TrackTileContent

fun TrackData.toTrackTileContent(): TrackTileContent {
    return TrackTileContent(
        trackID = this.trackID,
        albumCover = this.albumCover,
        trackTitle = this.trackTitle,
        artistName = this.artistName,
    )
}