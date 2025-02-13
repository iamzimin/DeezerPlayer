package com.evg.track_playback.presentation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.evg.resource.R
import com.evg.track_playback.presentation.mvi.TrackPlaybackSideEffect
import com.evg.track_playback.presentation.mvi.TrackPlaybackViewModel
import com.evg.track_playback.presentation.service.AudioService
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.snackbar.SnackBarEvent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun TrackPlaybackRoot(
    viewModel: TrackPlaybackViewModel = hiltViewModel<TrackPlaybackViewModel>(),
    modifier: Modifier,
    onPreviousScreen: () -> Unit,
) {
    val context = LocalContext.current
    val trackDownloadedString = stringResource(R.string.track_success_download)
    val trackRemoveString = stringResource(R.string.track_success_remove)
    var isServiceRunning by rememberSaveable { mutableStateOf(false) }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is TrackPlaybackSideEffect.StartService -> {
                if (!isServiceRunning) {
                    val intent = Intent(context, AudioService::class.java)
                    context.startForegroundService(intent)
                    isServiceRunning = true
                }
            }
            is TrackPlaybackSideEffect.TrackPlaybackFail -> {
                SnackBarController.sendEvent(SnackBarEvent(sideEffect.cause))
            }
            is TrackPlaybackSideEffect.PlaylistLoadFail -> {
                SnackBarController.sendEvent(SnackBarEvent(sideEffect.error.name)) //TODO
            }

            TrackPlaybackSideEffect.TrackDownloadSuccess -> {
                SnackBarController.sendEvent(SnackBarEvent(trackDownloadedString))
            }
            is TrackPlaybackSideEffect.TrackDownloadFail -> {
                SnackBarController.sendEvent(SnackBarEvent(sideEffect.cause)) //TODO
            }
            is TrackPlaybackSideEffect.TrackRemoveSuccess -> {
                SnackBarController.sendEvent(SnackBarEvent(trackRemoveString))
            }
        }
    }

    TrackPlaybackScreen(
        state = viewModel.collectAsState().value,
        dispatch = viewModel::dispatch,
        modifier = modifier,
        onPreviousScreen = onPreviousScreen,
    )
}