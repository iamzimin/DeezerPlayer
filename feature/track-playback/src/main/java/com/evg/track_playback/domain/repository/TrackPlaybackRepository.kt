package com.evg.track_playback.domain.repository

import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.track_playback.domain.model.TrackData

interface TrackPlaybackRepository {
    suspend fun getAlbumByTrackId(id: Long): ServerResult<List<TrackData>, NetworkError>
    suspend fun getTracksFromDatabase(): List<TrackData>
    suspend fun saveTrackToDatabase(track: TrackData)
}