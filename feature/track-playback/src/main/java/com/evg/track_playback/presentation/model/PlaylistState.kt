package com.evg.track_playback.presentation.model

import com.evg.track_playback.domain.model.TrackData

sealed class PlaylistState {
    data object Loading: PlaylistState()
    data object Error: PlaylistState()
    data class Ready(
        val trackLists: List<TrackData>,
        val currentPlayingIndex: Int?,
    ): PlaylistState()
}