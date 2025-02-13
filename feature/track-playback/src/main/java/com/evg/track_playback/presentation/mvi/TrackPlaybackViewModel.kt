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
import com.evg.track_playback.presentation.model.AudioState
import com.evg.track_playback.presentation.model.PlayerEvent
import com.evg.track_playback.presentation.model.UIState
import com.evg.track_playback.presentation.service.AudioServiceHandler
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
    private val trackId = savedStateHandle.get<Long>("id") ?: error("trackId is required")
    private val isOnlineMode = savedStateHandle.get<Boolean>("isOnlineMode") ?: error("isOnlineMode is required")

    private var trackList: List<TrackData> = emptyList()

    init {
        intent {
            postSideEffect(TrackPlaybackSideEffect.StartService)
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    AudioState.Initial -> reduce { state.copy(uiState = UIState.PlaylistLoading) }
                    is AudioState.Playing -> reduce { state.copy(isPlaying = mediaState.isPlaying) }
                    is AudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is AudioState.Ready -> {
                        reduce { state.copy(duration = mediaState.duration) }
                        audioServiceHandler.onPlayerEvents(PlayerEvent.Play)
                    }
                    is AudioState.PlayError -> {
                        reduce { state.copy(uiState = UIState.PlaylistLoadingError) }
                        postSideEffect(TrackPlaybackSideEffect.TrackPlaybackFail(cause = mediaState.cause))
                    }
                    is AudioState.CurrentPlaying -> {
                        val currentTrack = trackList.getOrNull(mediaState.mediaItemIndex)
                        currentTrack?.let {
                            reduce {
                                state.copy(uiState = UIState.Ready(trackLists = trackList, currentTrack = it))
                            }
                        }
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
                        Download.STATE_COMPLETED -> {
                            reduce { state.copy(isTrackDownloading = false) }

                            val track = getTrackFromPlaylistById(idStr = download.request.id)
                            track?.let {
                                trackPlaybackRepository.saveTrackToDatabase(track = it)
                            }

                            postSideEffect(TrackPlaybackSideEffect.TrackDownloadSuccess)
                        }
                        Download.STATE_REMOVING -> {
                            val track = getTrackFromPlaylistById(idStr = download.request.id)
                            track?.let {
                                trackPlaybackRepository.removeTrackByIdFromDatabase(id = it.trackID)
                            }

                            postSideEffect(TrackPlaybackSideEffect.TrackRemoveSuccess)
                        }
                        Download.STATE_FAILED -> {
                            val cause = finalException?.cause?.localizedMessage ?: "unknown"
                            reduce { state.copy(isTrackDownloading = false) }
                            postSideEffect(TrackPlaybackSideEffect.TrackDownloadFail(cause = cause))
                        }
                    }
                }
            }

            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {}
        })
    }

    init {
        if (isOnlineMode) {
            getAlbumListRemote(trackId = trackId)
        } else {
            getTracksLocal(trackId = trackId)
        }
    }


    fun dispatch(action: TrackPlaybackAction) = viewModelScope.launch {
        when (action) {
            TrackPlaybackAction.SaveTrack -> audioServiceHandler.onPlayerEvents(PlayerEvent.DownloadCurrentTrack)
            TrackPlaybackAction.RemoveTrack -> audioServiceHandler.onPlayerEvents(PlayerEvent.RemoveCurrentTrack)
            TrackPlaybackAction.SeekToPrev -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrev)
            TrackPlaybackAction.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            is TrackPlaybackAction.PlayPause -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            is TrackPlaybackAction.SeekTo -> {
                audioServiceHandler.onPlayerEvents(
                    PlayerEvent.SeekTo(((container.stateFlow.value.duration * action.position) / 100f).toLong()),
                )
            }
            is TrackPlaybackAction.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(PlayerEvent.UpdateProgress(action.newProgress))
                intent { reduce { state.copy(progress = action.newProgress) } }
            }
        }
    }

    private fun getAlbumListRemote(trackId: Long) = intent {
        viewModelScope.launch {
            reduce { state.copy(uiState = UIState.PlaylistLoading) }
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
                    reduce { state.copy(uiState = UIState.PlaylistLoadingError) }
                    postSideEffect(TrackPlaybackSideEffect.PlaylistLoadFail(error = response.error))
                }
            }
        }
    }

    private fun getTracksLocal(trackId: Long) = intent {
        viewModelScope.launch {
            reduce { state.copy(uiState = UIState.PlaylistLoading) }
            val response = trackPlaybackRepository.getTracksFromDatabase()
            trackList = response

            val startIndex = response.indexOfFirst {
                    track -> track.trackID == trackId
            }.let { if (it < 0) 0 else it }

            setMediaItems(
                trackLists = response,
                startIndex = startIndex,
            )
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

    private fun calculateProgressValue(currentProgress: Long) = intent {
        reduce {
            val progress = if (currentProgress > 0) ((currentProgress.toFloat() / state.duration.toFloat()) * 100f) else 0f
            state.copy(progress = progress)
        }
    }

    private fun getTrackFromPlaylistById(idStr: String): TrackData? {
        val id = idStr.toLongOrNull()
        return if (id != null) {
            trackList.find { it.trackID == id }
        } else { null }
    }

    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }
}

