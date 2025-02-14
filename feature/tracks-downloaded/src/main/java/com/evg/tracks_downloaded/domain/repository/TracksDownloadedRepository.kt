package com.evg.tracks_downloaded.domain.repository

import com.evg.tracks_downloaded.domain.model.TrackData
import kotlinx.coroutines.flow.Flow

interface TracksDownloadedRepository {
    suspend fun getTracksDownloadedFlow(): Flow<List<TrackData>>
}