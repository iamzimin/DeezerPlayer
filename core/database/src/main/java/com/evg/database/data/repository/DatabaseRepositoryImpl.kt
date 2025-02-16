package com.evg.database.data.repository

import android.database.sqlite.SQLiteFullException
import com.evg.database.data.storage.TracksDatabase
import com.evg.database.domain.models.TracksDBO
import com.evg.database.domain.repository.DatabaseRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.Flow

/**
 * Реализация репозитория для работы с локальной базой данных треков
 *
 * @param tracksDatabase База данных для управления треками
 */
class DatabaseRepositoryImpl(
    private val tracksDatabase: TracksDatabase
) : DatabaseRepository {
    /**
     * Получает список всех треков из базы данных
     *
     * @return Список объектов [TracksDBO]
     */
    override suspend fun getAllTracks(): List<TracksDBO> {
        return tracksDatabase.tracksDao.getAllTracks()
    }

    /**
     * Возвращает поток списка всех треков из базы данных
     *
     * @return Поток с данными [TracksDBO]
     */
    override fun getAllTracksFlow(): Flow<List<TracksDBO>> {
        return tracksDatabase.tracksDao.getAllTracksFlow()
    }

    /**
     * Добавляет трек в базу данных
     *
     * @param track Объект [TracksDBO] для вставки
     * @return Сохранена запись или нет
     */
    override suspend fun insertTrack(track: TracksDBO): Boolean {
        return try {
            tracksDatabase.tracksDao.insertTrack(track)
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    /**
     * Удаляет трек из базы данных по его идентификатору
     *
     * @param id Идентификатор трека
     */
    override suspend fun removeTrackById(id: Long) {
        tracksDatabase.tracksDao.removeTrackById(id = id)
    }

    /**
     * Получает трек из базы данных по его идентификатору
     *
     * @param id Идентификатор трека
     * @return Объект [TracksDBO] или null, если трек не найден
     */
    override suspend fun getTrackById(id: Long): TracksDBO? {
        return tracksDatabase.tracksDao.getTrackById(id)
    }
}
