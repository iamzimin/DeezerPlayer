package com.evg.database.data.repository

import com.evg.database.data.storage.TracksDatabase
import com.evg.database.domain.models.TracksDBO
import com.evg.database.domain.repository.DatabaseRepository

class DatabaseRepositoryImpl(
    private val tracksDatabase: TracksDatabase
) : DatabaseRepository {
    override suspend fun getAllTracks(): List<TracksDBO> {
        return tracksDatabase.tracksDao.getAllTracks()
    }

    override suspend fun insertTrack(track: TracksDBO) {
        tracksDatabase.tracksDao.insertTrack(track)
    }

    override suspend fun getTrackById(id: Long): TracksDBO? {
        return tracksDatabase.tracksDao.getTrackById(id)
    }
}
