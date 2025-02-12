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

class TrackPlaybackRepositoryImpl(
    private val apiRepository: DeezerApiRepository,
    private val databaseRepository: DatabaseRepository,
): TrackPlaybackRepository {
    override suspend fun getAlbumByTrackId(id: Long): ServerResult<List<TrackData>, NetworkError> {
        return apiRepository.getAlbumByTrackId(id = id).mapData { trackList ->
            trackList.map { trackResponse ->
                trackResponse.toTrackData()
            }
        }
    }

    override suspend fun getTracksFromDatabase(): List<TrackData> {
        return databaseRepository.getAllTracks().map {
            it.toTrackData()
        }
    }

    override suspend fun saveTrackToDatabase(track: TrackData) {
        databaseRepository.insertTrack(track = track.toTracksDBO())
    }
}