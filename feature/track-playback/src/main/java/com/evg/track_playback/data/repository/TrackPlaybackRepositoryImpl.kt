package com.evg.track_playback.data.repository

import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.api.domain.utils.mapData
import com.evg.database.domain.repository.DatabaseRepository
import com.evg.track_playback.domain.mapper.toTrackData
import com.evg.track_playback.domain.mapper.toTracksDBO
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.domain.repository.TrackPlaybackRepository

/**
 * Реализация репозитория для управления воспроизведением треков и их сохранением
 *
 * @param apiRepository Репозиторий для работы с API Deezer
 * @param databaseRepository Репозиторий для работы с локальной базой данных
 */
class TrackPlaybackRepositoryImpl(
    private val apiRepository: DeezerApiRepository,
    private val databaseRepository: DatabaseRepository,
): TrackPlaybackRepository {
    /**
     * Получает список треков альбома по идентификатору трека и отмечает скачанные треки
     *
     * @param id Идентификатор трека
     * @return Результат запроса, содержащий список треков или ошибку
     */
    override suspend fun getAlbumByTrackId(id: Long): ServerResult<List<TrackData>, NetworkError> {
        return apiRepository.getAlbumByTrackId(id = id).mapData { trackList ->
            val downloadedTrackIds = databaseRepository.getAllTracks().map { it.trackId }
            trackList.map { trackResponse ->
                trackResponse.toTrackData(isDownloaded = downloadedTrackIds.contains(trackResponse.id))
            }
        }
    }

    /**
     * Получает список загруженных треков из базы данных
     *
     * @return Список загруженных треков
     */
    override suspend fun getTracksFromDatabase(): List<TrackData> {
        return databaseRepository.getAllTracks().map {
            it.toTrackData()
        }
    }

    /**
     * Сохраняет трек в базе данных
     *
     * @param track Трек для сохранения
     */
    override suspend fun saveTrackToDatabase(track: TrackData) {
        databaseRepository.insertTrack(track = track.toTracksDBO())
    }

    /**
     * Удаляет трек из базы данных по его идентификатору
     *
     * @param id Идентификатор трека
     */
    override suspend fun removeTrackByIdFromDatabase(id: Long) {
        databaseRepository.removeTrackById(id = id)
    }
}