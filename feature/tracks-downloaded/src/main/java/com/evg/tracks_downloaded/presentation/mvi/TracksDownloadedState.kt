package com.evg.tracks_downloaded.presentation.mvi

import com.evg.ui.model.TrackTileContent

data class TracksDownloadedState(
    val isTracksLoading: Boolean = true,
    val tracksDownloaded: List<TrackTileContent> = emptyList(),
)