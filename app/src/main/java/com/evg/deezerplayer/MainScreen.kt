package com.evg.deezerplayer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.evg.chart.presentation.ChartRoot
import com.evg.deezerplayer.navigation.BottomBar
import com.evg.deezerplayer.navigation.DeezerPlayerScaffold
import com.evg.deezerplayer.navigation.Route
import com.evg.deezerplayer.navigation.TopBar
import com.evg.deezerplayer.navigation.bottomNavPadding
import com.evg.deezerplayer.snackbar.ObserveAsEvent
import com.evg.deezerplayer.snackbar.SwipeableSnackBarHost
import com.evg.track_playback.presentation.TrackPlaybackRoot
import com.evg.tracks_downloaded.presentation.TracksDownloadedRoot
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    var imageUrl: String? by remember { mutableStateOf(null) }
    var debouncedImageUrl: String? by remember { mutableStateOf(imageUrl) }
    val startDestination = Route.Chart

    LaunchedEffect(imageUrl) {
        if (imageUrl == null) {
            delay(1000L)
        }
        debouncedImageUrl = imageUrl
    }

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

    if (debouncedImageUrl != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(debouncedImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },
        containerColor = if (debouncedImageUrl == null) AppTheme.colors.background else Color.Transparent,
        snackbarHost = { SwipeableSnackBarHost(hostState = snackBarHostState) }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
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
                    TracksDownloadedRoot(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        onPlayerScreen = { id ->
                            navController.navigate(route = Route.TrackPlayer(id = id, isOnlineMode = false))
                        }
                    )
                }
            }
            composable<Route.TrackPlayer> {
                DeezerPlayerScaffold(
                    modifier = Modifier.padding(bottom = bottomNavPadding),
                    isContainerTransient = debouncedImageUrl != null,
                ) { paddingValues ->
                    TrackPlaybackRoot(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        onPreviousScreen = {
                            imageUrl = null
                            navController.popBackStack()
                        },
                        onBackgroundImageReady = {
                            imageUrl = it
                        }
                    )
                }
            }
        }
    }
}