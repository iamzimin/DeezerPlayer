package com.evg.track_playback.presentation.mvi

sealed class TrackPlaybackAction {
    data object SaveTrack: TrackPlaybackAction()

    data object PlayPause: TrackPlaybackAction()
    //data class SelectedAudioChange(val index: Int): TrackPlaybackAction()
    data class SeekTo(val position: Float): TrackPlaybackAction()
    data object SeekToNext: TrackPlaybackAction()
    data object SeekToPrev: TrackPlaybackAction()
    //data object Backward: TrackPlaybackAction()
    //data object Forward: TrackPlaybackAction()
    data class UpdateProgress(val newProgress: Float): TrackPlaybackAction()
}