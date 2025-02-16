package com.evg.database.domain.repository

import com.evg.database.domain.models.TracksDBO
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun getAllTracks(): List<TracksDBO>
    fun getAllTracksFlow(): Flow<List<TracksDBO>>
    suspend fun insertTrack(track: TracksDBO): Boolean
    suspend fun removeTrackById(id: Long)
    suspend fun getTrackById(id: Long): TracksDBO?
}