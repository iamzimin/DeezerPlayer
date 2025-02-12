package com.evg.track_playback.presentation.mvi

import com.evg.api.domain.utils.NetworkError

sealed class TrackPlaybackSideEffect {
    data class TrackPlaybackFail(val cause: String): TrackPlaybackSideEffect()
    data class PlaylistLoadFail(val error: NetworkError): TrackPlaybackSideEffect()
    data class TrackDownloadFail(val cause: String): TrackPlaybackSideEffect()
    data object TrackDownloadSuccess : TrackPlaybackSideEffect()
    data object StartService: TrackPlaybackSideEffect()
}