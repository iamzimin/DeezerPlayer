package com.evg.track_playback.presentation.handler

import com.evg.track_playback.domain.model.TrackData

class PlaylistHandler {
    private var playlist: List<TrackData> = emptyList()

    fun setPlaylist(tracks: List<TrackData>) {
        playlist = tracks
    }

    fun getPlaylist(): List<TrackData> = playlist

    fun findTrackById(trackId: Long): TrackData? =
        playlist.find { it.trackID == trackId }

    fun findTrackById(trackId: String): TrackData? {
        val id = trackId.toLongOrNull() ?: return null
        return findTrackById(id)
    }

    fun removeTrackAndComputeIndex(track: TrackData): Pair<List<TrackData>, Int?> {
        val removedIndex = playlist.indexOfFirst { it.trackID == track.trackID }
        val updatedList = playlist.filterNot { it.trackID == track.trackID }
        val newCurrentIndex = when {
            updatedList.isEmpty() -> null
            removedIndex == playlist.lastIndex -> updatedList.lastIndex
            else -> removedIndex
        }
        playlist = updatedList
        return updatedList to newCurrentIndex
    }

    fun toggleTrackDownloadStatus(track: TrackData, isDownloaded: Boolean) {
        playlist = playlist.map { currentTrack ->
            if (currentTrack.trackID == track.trackID)
                currentTrack.copy(isDownloaded = !isDownloaded)
            else
                currentTrack
        }
    }
}
