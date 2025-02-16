package com.evg.track_playback.presentation.mvi

import android.net.Uri
import androidx.annotation.OptIn
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import com.evg.api.domain.utils.ServerResult
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.domain.repository.TrackPlaybackRepository
import com.evg.track_playback.presentation.handler.AudioServiceHandler
import com.evg.track_playback.presentation.handler.PlaylistHandler
import com.evg.track_playback.presentation.model.AudioState
import com.evg.track_playback.presentation.model.PlayerEvent
import com.evg.track_playback.presentation.model.PlaylistState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

/**
 * ViewModel для управления воспроизведением треков
 *
 * @property audioServiceHandler Обработчик аудиосервиса
 * @property trackPlaybackRepository Репозиторий воспроизведения треков
 * @property savedStateHandle Сохраненное состояние
 * @property downloadManager Менеджер загрузок
 */
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

    private val playlistHandler = PlaylistHandler()

    init {
        initAudioStateCollecting()
        initDownloadManager(downloadManager)

        initPlaylist()
    }

    /**
     * Инициализирует сбор данных о состоянии аудиоплеера
     */
    private fun initAudioStateCollecting() = intent {
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
                    postSideEffect(TrackPlaybackSideEffect.TrackPlaybackFail(e = mediaState.e))
                }
                is AudioState.CurrentPlaying -> {
                    reduce { state.copy(
                        playlistState = PlaylistState.Ready(
                            trackLists = playlistHandler.getPlaylist(),
                            currentPlayingIndex = mediaState.mediaItemIndex,
                        ),
                    )}
                }
            }
        }
    }

    /**
     * Настраивает слушателя событий загрузки треков
     *
     * @param downloadManager Менеджер загрузок
     */
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
                    Download.STATE_FAILED -> trackDownloadFail(finalException)
                }
            }
            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
                removeTrackFromDB(download)
            }
        })
    }

    /**
     * Инициализирует плейлист
     */
    private fun initPlaylist() {
        if (isOnlineMode) {
            getAlbumListRemote(trackId)
        } else {
            getTracksLocal(trackId)
        }
    }


    /**
     * Обрабатывает действия пользователя
     *
     * @param action Действие пользователя
     */
    fun dispatch(action: TrackPlaybackAction) = intent {
        when (action) {
            TrackPlaybackAction.LoadPlaylist -> initPlaylist()
            TrackPlaybackAction.SaveTrack -> {
                reduce { state.copy(isTrackUpdating = true) }
                audioServiceHandler.onPlayerEvents(PlayerEvent.DownloadCurrentTrack)
            }
            TrackPlaybackAction.RemoveTrack -> {
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
                reduce { state.copy(progress = action.newProgress) }
            }
        }
    }


    /**
     * Загружает альбом с сервера по идентификатору трека
     *
     * @param trackId Идентификатор трека
     */
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

    /**
     * Получает треки из локальной базы данных
     *
     * @param trackId Идентификатор трека
     */
    private fun getTracksLocal(trackId: Long) = intent {
        reduce { state.copy(playlistState = PlaylistState.Loading) }
        val response = trackPlaybackRepository.getTracksFromDatabase()
        loadTracks(response, trackId)
    }

    /**
     * Загружает список треков и устанавливает медиа-элементы
     *
     * @param tracks Список треков
     * @param trackId Идентификатор текущего трека
     */
    private fun loadTracks(tracks: List<TrackData>, trackId: Long) = intent {
        playlistHandler.setPlaylist(tracks)
        val startIndex = tracks.indexOfFirst { it.trackID == trackId }.let { if (it < 0) 0 else it }
        setMediaItems(tracks, startIndex)
    }

    /**
     * Устанавливает список медиа-элементов в аудиосервис
     *
     * @param trackLists Список треков
     * @param startIndex Индекс начального трека
     */
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
                        .setArtist(audio.artistName)
                        .setDisplayTitle(audio.trackTitle)
                        .setAlbumTitle(audio.albumTitle)
                        .setArtworkUri(Uri.parse(audio.albumCover))
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


    /**
     * Сохраняет трек в базу данных после загрузки
     *
     * @param download Данные о загрузке трека
     */
    @OptIn(UnstableApi::class)
    private fun saveTrackToDB(download: Download) = intent {
        playlistHandler.findTrackById(download.request.id)?.let { track ->
            updateDownloadStatus(track, false)
        }
        reduce { state.copy(isTrackUpdating = false) }
    }

    /**
     * Удаляет трек из базы данных после удаления
     *
     * @param download Данные о загруженном треке
     */
    @OptIn(UnstableApi::class)
    private fun removeTrackFromDB(download: Download) = intent {
        playlistHandler.findTrackById(download.request.id)?.let { track ->
            updateDownloadStatus(track, true)
        }
    }

    /**
     * Обрабатывает ошибку загрузки трека
     *
     * @param exception Исключение, вызванное ошибкой загрузки
     */
    private fun trackDownloadFail(exception: Exception?) = intent {
        reduce { state.copy(isTrackUpdating = false) }
        postSideEffect(TrackPlaybackSideEffect.TrackDownloadFail(e = exception))
    }

    /**
     * Обновляет статус загрузки трека
     *
     * @param track Трек, статус которого обновляется
     * @param isDownloaded Флаг, указывающий, скачан ли трек
     */
    private fun updateDownloadStatus(
        track: TrackData,
        isDownloaded: Boolean,
    ) = intent {
        val uiState = container.stateFlow.value.playlistState
        if (uiState !is PlaylistState.Ready) return@intent

        val isSuccessUpdate = updateTrackInDatabase(track, isDownloaded)
        if (!isSuccessUpdate) return@intent

        if (!isOnlineMode && isDownloaded) {
            val (updatedList, newCurrentIndex) = playlistHandler.removeTrackAndComputeIndex(track)
            reduce { state.copy(
                playlistState = PlaylistState.Ready(
                    trackLists = updatedList,
                    currentPlayingIndex = newCurrentIndex,
                )
            )}
        } else {
            playlistHandler.toggleTrackDownloadStatus(track, isDownloaded)
            reduce { state.copy(
                playlistState = PlaylistState.Ready(
                    trackLists = playlistHandler.getPlaylist(),
                    currentPlayingIndex = uiState.currentPlayingIndex,
                )
            )}
        }
    }

    /**
     * Обновляет информацию о треке в базе данных
     *
     * @param track Трек для обновления
     * @param isDownloaded Флаг, указывающий, был ли трек удален
     * @param успешно ли обновление базы
     */
    private suspend fun Syntax<TrackPlaybackState, TrackPlaybackSideEffect>.updateTrackInDatabase(
        track: TrackData,
        isDownloaded: Boolean,
    ): Boolean {
        return if (isDownloaded) {
            trackPlaybackRepository.removeTrackByIdFromDatabase(track.trackID)
            postSideEffect(TrackPlaybackSideEffect.TrackRemoveSuccess)
            true
        } else {
            val isSuccessSave = trackPlaybackRepository.saveTrackToDatabase(track)
            if (isSuccessSave) {
                postSideEffect(TrackPlaybackSideEffect.TrackDownloadSuccess)
            } else {
                postSideEffect(TrackPlaybackSideEffect.TrackSaveFail)
            }
            isSuccessSave
        }
    }

    /**
     * Вычисляет процент выполнения трека
     *
     * @param currentProgress Текущая позиция воспроизведения
     */
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

    /**
     * Освобождает ресурсы при удалении ViewModel
     */
    override fun onCleared() {
        audioServiceHandler.onPlayerEvents(PlayerEvent.Stop)
        super.onCleared()
    }
}

