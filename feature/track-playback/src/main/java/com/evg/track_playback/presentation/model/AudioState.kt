package com.evg.track_playback.presentation.model

import androidx.media3.common.PlaybackException

sealed class AudioState {
    data object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class PlayError(val e: PlaybackException) : AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}