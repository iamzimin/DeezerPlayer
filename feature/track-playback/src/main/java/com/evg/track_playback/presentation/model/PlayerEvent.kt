package com.evg.track_playback.presentation.model

sealed class PlayerEvent {
    data object DownloadCurrentTrack : PlayerEvent()
    data class RemoveCurrentTrack(val isRemoveCurrentMedia: Boolean) : PlayerEvent()
    data object Play : PlayerEvent()
    data object PlayPause : PlayerEvent()
    data object SeekToPrev : PlayerEvent()
    data object SeekToNext : PlayerEvent()
    data object Stop : PlayerEvent()
    data class PlayByIndex(val index: Int) : PlayerEvent()
    data class SeekTo(val seekPosition: Long) : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}