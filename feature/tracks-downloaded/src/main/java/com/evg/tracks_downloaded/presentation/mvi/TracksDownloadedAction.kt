package com.evg.tracks_downloaded.presentation.mvi

sealed class TracksDownloadedAction {
    data class FilterTracksOnScreen(val query: String): TracksDownloadedAction()
}