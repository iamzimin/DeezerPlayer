package com.evg.deezerplayer.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.evg.resource.R


sealed class BottomBarScreen(
    val route: Route,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    companion object {
        val allScreens = listOf(Chart, DownloadedTracks)
    }

    data object Chart : BottomBarScreen(
        route = Route.Chart,
        title = R.string.chart,
        icon = Icons.Default.Home,
    )
    data object DownloadedTracks : BottomBarScreen(
        route = Route.DownloadedTracks,
        title = R.string.downloaded,
        icon = Icons.Default.Favorite,
    )
}
