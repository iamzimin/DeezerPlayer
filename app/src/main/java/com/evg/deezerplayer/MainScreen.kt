package com.evg.deezerplayer

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.evg.chart.presentation.ChartRoot
import com.evg.deezerplayer.navigation.BottomBar
import com.evg.deezerplayer.navigation.DeezerPlayerScaffold
import com.evg.deezerplayer.navigation.Route
import com.evg.deezerplayer.navigation.bottomNavPadding
import com.evg.deezerplayer.snackbar.ObserveAsEvent
import com.evg.deezerplayer.snackbar.SwipeableSnackBarHost
import com.evg.track_playback.presentation.TrackPlaybackRoot
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    val startDestination = Route.Chart

    val scope = rememberCoroutineScope()
    ObserveAsEvent(
        flow = SnackBarController.events,
        snackBarHostState,
    ) { event ->
        scope.launch {
            snackBarHostState.currentSnackbarData?.dismiss()
            val result = snackBarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Short,
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        bottomBar = { BottomBar(navController) },
        containerColor = AppTheme.colors.background,
        snackbarHost = { SwipeableSnackBarHost(hostState = snackBarHostState) }
    ) {
        SharedTransitionLayout {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.background(AppTheme.colors.background),
            ) {
                composable<Route.Chart> {
                    DeezerPlayerScaffold(
                        modifier = Modifier.padding(bottom = bottomNavPadding)
                    ) { paddingValues ->
                        ChartRoot(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            onPlayerScreen = { id ->
                                navController.navigate(route = Route.TrackPlayer(id = id, isOnlineMode = true))
                            }
                        )
                    }
                }
                composable<Route.DownloadedTracks> {
                    DeezerPlayerScaffold(
                        modifier = Modifier.padding(bottom = bottomNavPadding)
                    ) { paddingValues ->

                    }
                }
                composable<Route.TrackPlayer> {
                    DeezerPlayerScaffold { paddingValues ->
                        TrackPlaybackRoot(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                        )
                    }
                }
            }
        }
    }
}