package com.evg.track_playback.presentation.mvi

sealed class TrackPlaybackAction {
    data object SaveTrack: TrackPlaybackAction()
    data object RemoveTrack: TrackPlaybackAction()

    data object PlayPause: TrackPlaybackAction()
    data class SeekTo(val position: Float): TrackPlaybackAction()
    data class PlayByIndex(val index: Int): TrackPlaybackAction()
    data object SeekToNext: TrackPlaybackAction()
    data object SeekToPrev: TrackPlaybackAction()
    //data object Backward: TrackPlaybackAction()
    //data object Forward: TrackPlaybackAction()
    data class UpdateProgress(val newProgress: Float): TrackPlaybackAction()
}