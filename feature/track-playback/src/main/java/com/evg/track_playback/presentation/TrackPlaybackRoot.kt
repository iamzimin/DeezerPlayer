package com.evg.track_playback.presentation

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.evg.resource.R
import com.evg.track_playback.presentation.mapper.getLocalizedExceptionMessage
import com.evg.track_playback.presentation.mapper.getLocalizedPlayback
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.track_playback.presentation.mvi.TrackPlaybackSideEffect
import com.evg.track_playback.presentation.mvi.TrackPlaybackViewModel
import com.evg.track_playback.presentation.service.AudioService
import com.evg.ui.mapper.toErrorMessage
import com.evg.ui.snackbar.SnackBarAction
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.snackbar.SnackBarEvent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * Корневой экран воспроизведения трека
 *
 * @param viewModel ViewModel для управления воспроизведением
 * @param modifier Модификатор для стилизации
 * @param onPreviousScreen Функция для возврата на предыдущий экран
 * @param onBackgroundImageReady Функция отправки в root url изображения
 */
@Composable
fun TrackPlaybackRoot(
    viewModel: TrackPlaybackViewModel = hiltViewModel<TrackPlaybackViewModel>(),
    modifier: Modifier,
    onPreviousScreen: () -> Unit,
    onBackgroundImageReady: (url: String) -> Unit,
) {
    BackHandler {
        onPreviousScreen()
    }

    val context = LocalContext.current
    val trackDownloadedString = stringResource(R.string.track_success_download)
    val trackSaveErrorString = stringResource(R.string.error_track_save)
    val updateString = stringResource(id = R.string.update)
    val trackRemoveString = stringResource(R.string.track_success_remove)
    var isServiceRunning by rememberSaveable { mutableStateOf(false) }

    val activity = (LocalActivity.current as Activity)

    DisposableEffect(Unit) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

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
                SnackBarController.sendEvent(SnackBarEvent(getLocalizedPlayback(context, sideEffect.e)))
            }
            is TrackPlaybackSideEffect.PlaylistLoadFail -> {
                SnackBarController.sendEvent(SnackBarEvent(
                    message = sideEffect.error.toErrorMessage(context),
                    action = SnackBarAction(
                        name = updateString,
                        action = { viewModel.dispatch(TrackPlaybackAction.LoadPlaylist) },
                    ),
                ))
            }

            TrackPlaybackSideEffect.TrackDownloadSuccess -> {
                SnackBarController.sendEvent(SnackBarEvent(trackDownloadedString))
            }
            is TrackPlaybackSideEffect.TrackDownloadFail -> {
                SnackBarController.sendEvent(SnackBarEvent(getLocalizedExceptionMessage(context, sideEffect.e)))
            }
            TrackPlaybackSideEffect.TrackSaveFail -> {
                SnackBarController.sendEvent(SnackBarEvent(trackSaveErrorString))
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
        onBackgroundImageReady = onBackgroundImageReady,
    )
}