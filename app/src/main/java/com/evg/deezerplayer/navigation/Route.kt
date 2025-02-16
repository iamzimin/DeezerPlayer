package com.evg.deezerplayer.navigation

import kotlinx.serialization.Serializable

/**
 * Определяет маршруты навигации в приложении
 */
sealed interface Route {
    @Serializable data object Chart: Route
    @Serializable data object DownloadedTracks: Route
    @Serializable data class TrackPlayer(val id: Long, val isOnlineMode: Boolean): Route
}
