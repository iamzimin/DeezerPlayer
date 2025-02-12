package com.evg.track_playback.presentation.model

import com.evg.track_playback.domain.model.TrackData

sealed class UIState {
    data object PlaylistLoading: UIState()
    data object PlaylistLoadingError: UIState()
    data class Ready(val trackLists: List<TrackData>, val currentTrack: TrackData): UIState()
}