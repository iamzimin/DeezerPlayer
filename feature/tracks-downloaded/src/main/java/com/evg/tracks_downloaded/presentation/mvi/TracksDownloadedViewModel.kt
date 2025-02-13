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
        getTracksDownloaded()
    }

    fun dispatch(action: TracksDownloadedAction) {
        when (action) {
            TracksDownloadedAction.GetTracksDownloaded -> getTracksDownloaded()
            is TracksDownloadedAction.SearchTracksDownloaded -> searchTracksDownloaded(query = action.query)
            is TracksDownloadedAction.FilterTracksOnScreen -> searchTrack(query = action.query)
        }
    }

    private fun getTracksDownloaded() = intent {
        reduce { state.copy(isTracksLoading = true) }
        val response = tracksDownloadedRepository.getTracksDownloaded().map { it.toTrackTileContent() }
        cachedTracks = response
        reduce { state.copy(
            isTracksLoading = false,
            tracksDownloaded = response,
        ) }
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

    private fun searchTracksDownloaded(query: String) = intent {
        reduce { state.copy(isTracksLoading = true) }
        val response = tracksDownloadedRepository.getTracksDownloaded().map { it.toTrackTileContent() }
        cachedTracks = response
        val filteredTracks = response.filter {
            it.trackTitle.contains(query, ignoreCase = true) ||
            it.artistName.contains(query, ignoreCase = true)
        }
        reduce { state.copy(
            isTracksLoading = false,
            tracksDownloaded = filteredTracks,
        ) }
    }
}
