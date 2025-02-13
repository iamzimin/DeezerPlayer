package com.evg.tracks_downloaded.domain.repository

import com.evg.tracks_downloaded.domain.model.TrackData

interface TracksDownloadedRepository {
    suspend fun getTracksDownloaded(): List<TrackData>
}