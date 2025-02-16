package com.evg.tracks_downloaded.data.repository

import com.evg.database.domain.repository.DatabaseRepository
import com.evg.tracks_downloaded.domain.mapper.toTrackData
import com.evg.tracks_downloaded.domain.model.TrackData
import com.evg.tracks_downloaded.domain.repository.TracksDownloadedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Реализация репозитория загруженных треков
 *
 * @param databaseRepository Репозиторий базы данных для получения загруженных треков
 */
class TracksDownloadedRepositoryImpl(
    private val databaseRepository: DatabaseRepository,
): TracksDownloadedRepository {
    /**
     * Возвращает поток списка загруженных треков
     *
     * @return Поток списка треков, преобразованных из базы данных
     */
    override suspend fun getTracksDownloadedFlow(): Flow<List<TrackData>> {
        return databaseRepository.getAllTracksFlow().map { list ->
            list.map { trackDBO ->
                trackDBO.toTrackData()
            }
        }
    }
}