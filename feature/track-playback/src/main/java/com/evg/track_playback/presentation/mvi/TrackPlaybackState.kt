package com.evg.track_playback.presentation.mvi

import com.evg.track_playback.presentation.model.PlaylistState

data class TrackPlaybackState(
    val playlistState: PlaylistState = PlaylistState.Loading,
    val isTrackUpdating: Boolean = false,
    val duration: Long = 0,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
)