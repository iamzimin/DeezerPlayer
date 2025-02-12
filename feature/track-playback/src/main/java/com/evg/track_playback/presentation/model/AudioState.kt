package com.evg.track_playback.presentation.model

sealed class AudioState {
    data object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class PlayError(val cause: String) : AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}