package com.evg.track_playback.presentation.mvi

import androidx.annotation.OptIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import com.evg.api.domain.utils.ServerResult
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.domain.repository.TrackPlaybackRepository
import com.evg.track_playback.presentation.service.AudioServiceHandler
import com.evg.track_playback.presentation.service.AudioState
import com.evg.track_playback.presentation.service.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TrackPlaybackViewModel @OptIn(UnstableApi::class) @Inject constructor(
    private val audioServiceHandler: AudioServiceHandler,
    private val trackPlaybackRepository: TrackPlaybackRepository,
    savedStateHandle: SavedStateHandle,
    downloadManager: DownloadManager,
): ContainerHost<TrackPlaybackState, TrackPlaybackSideEffect>, ViewModel() {
    override val container = container<TrackPlaybackState, TrackPlaybackSideEffect>(TrackPlaybackState())
    private val trackId: Long = savedStateHandle.get<Long>("id") ?: error("Track ID is required")
    private var trackList: List<TrackData> = emptyList()

    init {
        intent {
            postSideEffect(TrackPlaybackSideEffect.StartService)
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    AudioState.Initial -> reduce { state.copy(uiState = UIState.Initial) }
                    is AudioState.Buffering -> calculateProgressValue(mediaState.progress)
                    is AudioState.Playing -> reduce { state.copy(isPlaying = mediaState.isPlaying) }
                    is AudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is AudioState.Ready -> {
                        reduce {
                            state.copy(
                                duration = mediaState.duration,
                                uiState = UIState.Ready,
                            )
                        }
                    }
                    is AudioState.PlayError -> postSideEffect(TrackPlaybackSideEffect.TrackPlaybackFail(cause = mediaState.cause))
                    is AudioState.CurrentPlaying -> {
                        reduce { state.copy(currentSelectedTrack = trackList[mediaState.mediaItemIndex]) }
                    }
                }
            }
        }
    }

    init {
        downloadManager.addListener(object : DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: DownloadManager,
                download: Download,
                finalException: Exception?,
            ) {
                intent {
                    when (download.state) {
                        Download.STATE_QUEUED -> {}
                        Download.STATE_STOPPED -> {}
                        Download.STATE_DOWNLOADING -> {}
                        Download.STATE_COMPLETED -> {
                            reduce { state.copy(isTrackDownloading = false) }
                            trackPlaybackRepository.saveTrackToDatabase(
                                track = container.stateFlow.value.currentSelectedTrack //TODO!!!
                            )
                            postSideEffect(TrackPlaybackSideEffect.TrackDownloadSuccess)
                        }
                        Download.STATE_FAILED -> {
                            val cause = finalException?.cause?.localizedMessage ?: "unknown"
                            reduce { state.copy(isTrackDownloading = false) }
                            postSideEffect(TrackPlaybackSideEffect.TrackDownloadFail(cause = cause))
                        }
                        Download.STATE_REMOVING -> {}
                        Download.STATE_RESTARTING -> {}
                    }
                }
            }

            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {}
        })
    }

    init {
        getAlbumListRemote(trackId = trackId)
    }


    fun dispatch(action: TrackPlaybackAction) = viewModelScope.launch {
        when (action) {
            TrackPlaybackAction.SaveTrack -> audioServiceHandler.onPlayerEvents(PlayerEvent.DownloadCurrentTrack)
            TrackPlaybackAction.SeekToPrev -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrev)
            TrackPlaybackAction.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            is TrackPlaybackAction.PlayPause -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            is TrackPlaybackAction.SeekTo -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo,
                    seekPosition = ((container.stateFlow.value.duration * action.position) / 100f).toLong()
                )
            }
            is TrackPlaybackAction.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(PlayerEvent.UpdateProgress(action.newProgress))
                intent { reduce { state.copy(progress = action.newProgress) } }
            }
            /*is TrackPlaybackAction.SelectedAudioChange -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SelectedAudioChange,
                    selectedAudioIndex = action.index
                )
            }
            TrackPlaybackAction.Backward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Backward)
            TrackPlaybackAction.Forward -> audioServiceHandler.onPlayerEvents(PlayerEvent.Forward)
            */
        }
    }

    private fun getAlbumListRemote(trackId: Long) = intent {
        viewModelScope.launch {
            reduce { state.copy(isPlaylistLoading = true) }
            when (val response = trackPlaybackRepository.getAlbumByTrackId(id = trackId)) {
                is ServerResult.Success -> {
                    trackList = response.data

                    val startIndex = response.data.indexOfFirst {
                        track -> track.trackID == trackId
                    }.let { if (it < 0) 0 else it }
                    setMediaItems(
                        trackLists = response.data,
                        startIndex = startIndex,
                    )
                }
                is ServerResult.Error -> {
                    postSideEffect(TrackPlaybackSideEffect.PlaylistLoadFail(error = response.error))
                }
            }
            reduce { state.copy(isPlaylistLoading = false) }
        }
    }

    private fun getTracksLocal(trackId: Long) = intent {
        viewModelScope.launch {
            reduce { state.copy(isPlaylistLoading = true) }
            val response = trackPlaybackRepository.getTracksFromDatabase()
            trackList = response

            val startIndex = response.indexOfFirst {
                    track -> track.trackID == trackId
            }.let { if (it < 0) 0 else it }

            setMediaItems(
                trackLists = response,
                startIndex = startIndex,
            )
            reduce { state.copy(isPlaylistLoading = false) }
        }
    }

    private fun setMediaItems(trackLists: List<TrackData>, startIndex: Int) {
        trackLists.map { audio ->
            MediaItem.Builder()
                .setUri(audio.trackPreview)
                .setLiveConfiguration(
                    MediaItem.LiveConfiguration.Builder().setMaxPlaybackSpeed(1.02f).build()
                )
                .setMediaId(audio.trackID.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artistName)
                        .setDisplayTitle(audio.trackTitle)
                        .build()
                )
                .build()
        }.also { mediaItems ->
            audioServiceHandler.setMediaItemList(
                mediaItems = mediaItems,
                startIndex = startIndex,
            )
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }

    private fun calculateProgressValue(currentProgress: Long) = intent {
        reduce {
            val progress = if (currentProgress > 0) ((currentProgress.toFloat() / state.duration.toFloat()) * 100f) else 0f
            state.copy(progress = progress)
        }
    }
}

sealed class UIState {
    data object Initial: UIState()
    data object Ready: UIState()
}
