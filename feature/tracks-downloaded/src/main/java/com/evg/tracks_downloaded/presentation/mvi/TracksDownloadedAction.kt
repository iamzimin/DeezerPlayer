package com.evg.tracks_downloaded.presentation.mvi

sealed class TracksDownloadedAction {
    data object GetTracksDownloaded: TracksDownloadedAction()
    data class SearchTracksDownloaded(val query: String): TracksDownloadedAction()
    data class FilterTracksOnScreen(val query: String): TracksDownloadedAction()
}