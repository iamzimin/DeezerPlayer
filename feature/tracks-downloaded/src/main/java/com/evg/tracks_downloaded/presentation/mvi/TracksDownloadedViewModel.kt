package com.evg.tracks_downloaded.presentation.mvi

import androidx.lifecycle.ViewModel
import com.evg.tracks_downloaded.domain.repository.TracksDownloadedRepository
import com.evg.tracks_downloaded.presentation.mapper.toTrackTileContent
import com.evg.ui.model.TrackTileContent
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class TracksDownloadedViewModel @Inject constructor(
    private val tracksDownloadedRepository: TracksDownloadedRepository
): ContainerHost<TracksDownloadedState, TracksDownloadedSideEffect>, ViewModel() {
    override val container = container<TracksDownloadedState, TracksDownloadedSideEffect>(TracksDownloadedState())

    private var cachedTracks: List<TrackTileContent> = emptyList()

    init {
        subscribeToDatabaseChanges()
    }

    private fun subscribeToDatabaseChanges() = intent {
        tracksDownloadedRepository.getTracksDownloadedFlow().collect { tracks ->
            reduce { state.copy(isTracksLoading = true) }
            val trackTileContents = tracks.map { it.toTrackTileContent() }
            cachedTracks = trackTileContents
            reduce { state.copy(
                isTracksLoading = false,
                tracksDownloaded = trackTileContents
            ) }
        }
    }


    fun dispatch(action: TracksDownloadedAction) {
        when (action) {
            is TracksDownloadedAction.FilterTracksOnScreen -> searchTrack(query = action.query)
        }
    }

    private fun searchTrack(query: String) = intent {
        val filteredTracks = if (query.isBlank()) {
            cachedTracks
        } else {
            cachedTracks.filter {
                it.trackTitle.contains(query, ignoreCase = true) ||
                it.artistName.contains(query, ignoreCase = true)
            }
        }
        reduce { state.copy(tracksDownloaded = filteredTracks) }
    }
}
