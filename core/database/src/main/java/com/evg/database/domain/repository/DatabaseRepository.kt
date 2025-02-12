package com.evg.database.domain.repository

import com.evg.database.domain.models.TracksDBO

interface DatabaseRepository {
    suspend fun getAllTracks(): List<TracksDBO>
    suspend fun insertTrack(track: TracksDBO)
    suspend fun getTrackById(id: Long): TracksDBO?
}