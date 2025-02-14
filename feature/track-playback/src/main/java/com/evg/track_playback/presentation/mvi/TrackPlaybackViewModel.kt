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
import com.evg.track_playback.presentation.model.PlaylistState
import com.evg.track_playback.presentation.service.AudioServiceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
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
        initAudioStateCollecting()
        initDownloadManager(downloadManager)

        if (isOnlineMode) {
            getAlbumListRemote(trackId)
        } else {
            getTracksLocal(trackId)
        }
    }

    private fun initAudioStateCollecting() {
        intent {
            postSideEffect(TrackPlaybackSideEffect.StartService)
            audioServiceHandler.audioState.collectLatest { mediaState ->
                when (mediaState) {
                    AudioState.Initial -> reduce { state.copy(playlistState = PlaylistState.Loading) }
                    is AudioState.Playing -> reduce { state.copy(isPlaying = mediaState.isPlaying) }
                    is AudioState.Progress -> calculateProgressValue(mediaState.progress)
                    is AudioState.Ready -> {
                        reduce { state.copy(duration = mediaState.duration) }
                        audioServiceHandler.onPlayerEvents(PlayerEvent.Play)
                    }
                    is AudioState.PlayError -> {
                        reduce { state.copy(playlistState = PlaylistState.Error) }
                        postSideEffect(TrackPlaybackSideEffect.TrackPlaybackFail(cause = mediaState.cause))
                    }
                    is AudioState.CurrentPlaying -> {
                        reduce { state.copy(
                            playlistState = PlaylistState.Ready(
                                trackLists = trackList,
                                currentPlayingIndex = mediaState.mediaItemIndex,
                            ),
                        )}
                    }
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initDownloadManager(downloadManager: DownloadManager) {
        downloadManager.addListener(object : DownloadManager.Listener {
            override fun onDownloadChanged(
                downloadManager: DownloadManager,
                download: Download,
                finalException: Exception?,
            ) {
                when (download.state) {
                    Download.STATE_COMPLETED -> saveTrackToDB(download)
                    Download.STATE_REMOVING -> removeTrackFromDB(download)
                    Download.STATE_FAILED -> trackDownloadFail(finalException)
                }
            }
            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {}
        })
    }



    fun dispatch(action: TrackPlaybackAction) = viewModelScope.launch {
        when (action) {
            TrackPlaybackAction.SaveTrack -> {
                intent { reduce { state.copy(isTrackUpdating = true) } }
                audioServiceHandler.onPlayerEvents(PlayerEvent.DownloadCurrentTrack)
            }
            TrackPlaybackAction.RemoveTrack -> {
                intent { reduce { state.copy(isTrackUpdating = true) } }
                audioServiceHandler.onPlayerEvents(PlayerEvent.RemoveCurrentTrack(!isOnlineMode))
            }
            TrackPlaybackAction.SeekToPrev -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToPrev)
            TrackPlaybackAction.SeekToNext -> audioServiceHandler.onPlayerEvents(PlayerEvent.SeekToNext)
            is TrackPlaybackAction.PlayPause -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
            is TrackPlaybackAction.PlayByIndex -> audioServiceHandler.onPlayerEvents(PlayerEvent.PlayByIndex(action.index))
            is TrackPlaybackAction.SeekTo -> {
                val position = ((container.stateFlow.value.duration * action.position) / 100f).toLong()
                audioServiceHandler.onPlayerEvents(PlayerEvent.SeekTo(seekPosition = position))
            }
            is TrackPlaybackAction.UpdateProgress -> {
                audioServiceHandler.onPlayerEvents(PlayerEvent.UpdateProgress(action.newProgress))
                intent { reduce { state.copy(progress = action.newProgress) } }
            }
        }
    }



    private fun loadTracks(tracks: List<TrackData>, trackId: Long) = intent {
        trackList = tracks
        val startIndex = tracks.indexOfFirst { it.trackID == trackId }.let { if (it < 0) 0 else it }
        setMediaItems(tracks, startIndex)
    }

    private fun getAlbumListRemote(trackId: Long) = intent {
        reduce { state.copy(playlistState = PlaylistState.Loading) }
        when (val response = trackPlaybackRepository.getAlbumByTrackId(id = trackId)) {
            is ServerResult.Success -> {
                loadTracks(response.data, trackId)
            }
            is ServerResult.Error -> {
                reduce { state.copy(playlistState = PlaylistState.Error) }
                postSideEffect(TrackPlaybackSideEffect.PlaylistLoadFail(error = response.error))
            }
        }
    }

    private fun getTracksLocal(trackId: Long) = intent {
        reduce { state.copy(playlistState = PlaylistState.Loading) }
        val response = trackPlaybackRepository.getTracksFromDatabase()
        loadTracks(response, trackId)
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



    private fun updateDownloadStatus(
        track: TrackData,
        isDownloaded: Boolean,
    ) = intent {
        val uiState = container.stateFlow.value.playlistState
        if (uiState !is PlaylistState.Ready) return@intent

        updateTrackInDatabase(track, isDownloaded)

        if (!isOnlineMode && isDownloaded) {
            val (updatedList, newCurrentIndex) = removeTrackAndComputeIndex(track)
            trackList = updatedList
            reduce { state.copy(
                playlistState = PlaylistState.Ready(
                    trackLists = trackList,
                    currentPlayingIndex = newCurrentIndex,
                )
            )}
        } else {
            trackList = toggleTrackDownloadStatus(track, isDownloaded)
            reduce { state.copy(
                playlistState = PlaylistState.Ready(
                    trackLists = trackList,
                    currentPlayingIndex = uiState.currentPlayingIndex,
                )
            )}
        }
    }

    private suspend fun Syntax<TrackPlaybackState, TrackPlaybackSideEffect>.updateTrackInDatabase(
        track: TrackData,
        isDownloaded: Boolean,
    ) {
        if (isDownloaded) {
            trackPlaybackRepository.removeTrackByIdFromDatabase(track.trackID)
            postSideEffect(TrackPlaybackSideEffect.TrackRemoveSuccess)
        } else {
            trackPlaybackRepository.saveTrackToDatabase(track)
            postSideEffect(TrackPlaybackSideEffect.TrackDownloadSuccess)
        }
    }

    private fun removeTrackAndComputeIndex(track: TrackData): Pair<List<TrackData>, Int?> {
        val removedIndex = trackList.indexOfFirst { it.trackID == track.trackID }
        val updatedList = trackList.filterNot { it.trackID == track.trackID }
        val newCurrentIndex = when {
            updatedList.isEmpty() -> null
            removedIndex == trackList.lastIndex -> updatedList.lastIndex
            else -> removedIndex
        }
        return updatedList to newCurrentIndex
    }

    private fun toggleTrackDownloadStatus(track: TrackData, isDownloaded: Boolean): List<TrackData> {
        return trackList.map { currentTrack ->
            if (currentTrack.trackID == track.trackID)
                currentTrack.copy(isDownloaded = !isDownloaded)
            else
                currentTrack
        }
    }


    @OptIn(UnstableApi::class)
    private fun saveTrackToDB(download: Download) = intent {
        getTrackFromPlaylistById(download.request.id)?.let { track ->
            updateDownloadStatus(track, false)
        }
        reduce { state.copy(isTrackUpdating = false) }
    }

    @OptIn(UnstableApi::class)
    private fun removeTrackFromDB(download: Download) = intent {
        getTrackFromPlaylistById(download.request.id)?.let { track ->
            updateDownloadStatus(track, true)
        }
        reduce { state.copy(isTrackUpdating = false) }
    }

    private fun trackDownloadFail(finalException: Exception?) = intent {
        val cause = finalException?.cause?.localizedMessage ?: "unknown" //TODO
        reduce { state.copy(isTrackUpdating = false) }
        postSideEffect(TrackPlaybackSideEffect.TrackDownloadFail(cause = cause))
    }

    private fun calculateProgressValue(currentProgress: Long) = intent {
        reduce {
            val progress = if (state.duration > 0 && currentProgress > 0) {
                (currentProgress.toFloat() / state.duration.toFloat()) * 100f
            } else {
                0f
            }

            state.copy(progress = progress)
        }
    }

    private fun getTrackFromPlaylistById(idStr: String): TrackData? {
        val id = idStr.toLongOrNull()
        return trackList.find { it.trackID == id }
    }

    override fun onCleared() {
        viewModelScope.launch {
            audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        }
        super.onCleared()
    }
}

