package com.evg.track_playback.presentation.mvi

import androidx.media3.common.PlaybackException
import com.evg.api.domain.utils.NetworkError
import java.lang.Exception

sealed class TrackPlaybackSideEffect {
    data object StartService: TrackPlaybackSideEffect()

    data class TrackPlaybackFail(val e: PlaybackException): TrackPlaybackSideEffect()
    data class PlaylistLoadFail(val error: NetworkError): TrackPlaybackSideEffect()

    data object TrackDownloadSuccess : TrackPlaybackSideEffect()
    data class TrackDownloadFail(val e: Exception?): TrackPlaybackSideEffect()
    data object TrackRemoveSuccess : TrackPlaybackSideEffect()
}