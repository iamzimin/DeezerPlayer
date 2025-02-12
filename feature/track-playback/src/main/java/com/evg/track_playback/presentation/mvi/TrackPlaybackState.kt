package com.evg.track_playback.presentation.mvi

import com.evg.track_playback.presentation.model.UIState

data class TrackPlaybackState(
    val uiState: UIState = UIState.PlaylistLoading,
    val isTrackDownloading: Boolean = false,
    val duration: Long = 0,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
)