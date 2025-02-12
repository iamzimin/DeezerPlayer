package com.evg.track_playback.presentation.mvi

import com.evg.track_playback.domain.model.TrackData

data class TrackPlaybackState(
    val isPlaylistLoading: Boolean = false,
    val uiState: UIState = UIState.Initial,
    val duration: Long = 0,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
    val currentSelectedTrack: TrackData = TrackData(0,"","", "", 0,""),
)