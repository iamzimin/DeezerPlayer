package com.evg.deezerplayer.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Chart: Route
    @Serializable data object DownloadedTracks: Route
    @Serializable data class TrackPlayer(val id: Long): Route
}
