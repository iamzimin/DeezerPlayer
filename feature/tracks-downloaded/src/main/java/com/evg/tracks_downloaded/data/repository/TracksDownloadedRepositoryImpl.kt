package com.evg.tracks_downloaded.data.repository

import com.evg.database.domain.repository.DatabaseRepository
import com.evg.tracks_downloaded.domain.mapper.toTrackData
import com.evg.tracks_downloaded.domain.model.TrackData
import com.evg.tracks_downloaded.domain.repository.TracksDownloadedRepository

class TracksDownloadedRepositoryImpl(
    private val databaseRepository: DatabaseRepository,
): TracksDownloadedRepository {
    override suspend fun getTracksDownloaded(): List<TrackData> {
        return databaseRepository.getAllTracks().map {
            it.toTrackData()
        }
    }
}