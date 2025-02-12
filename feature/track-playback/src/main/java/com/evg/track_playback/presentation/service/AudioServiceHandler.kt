package com.evg.track_playback.presentation.service


import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer,
) : Player.Listener {
    private val _audioState: MutableStateFlow<AudioState> =
        MutableStateFlow(AudioState.Initial)
    val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var job: Job? = null

    init {
        exoPlayer.addListener(this)

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExoPlayer", "Ошибка: ${error.message}", error.cause)
                _audioState.value = AudioState.Error(cause = error.cause?.toString() ?: "unknown") //TODO
            }
        })
    }

    fun setMediaItemList(mediaItems: List<MediaItem>, startIndex: Int) {
        serviceScope.launch {
            exoPlayer.setMediaItems(mediaItems, startIndex, 0)
            exoPlayer.prepare()
        }
    }

    suspend fun onPlayerEvents(
        playerEvent: PlayerEvent,
        seekPosition: Long = 0,
    ) {
        when (playerEvent) {
            PlayerEvent.SeekToPrev -> exoPlayer.seekToPrevious()
            PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            PlayerEvent.PlayPause -> playOrPause()
            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo(
                    (exoPlayer.duration * playerEvent.newProgress).toLong()
                )
            }

            /*PlayerEvent.SelectedAudioChange -> {
                when (selectedAudioIndex) {
                    exoPlayer.currentMediaItemIndex -> {
                        playOrPause()
                    }

                    else -> {
                        exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                        _audioState.value = AudioState.Playing(
                            isPlaying = true
                        )
                        exoPlayer.playWhenReady = true
                        startProgressUpdate()
                    }
                }
            }
            PlayerEvent.Backward -> exoPlayer.seekBack()
            PlayerEvent.Forward -> exoPlayer.seekForward()
            */
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioState.value =
                AudioState.Buffering(exoPlayer.currentPosition)

            ExoPlayer.STATE_READY -> _audioState.value =
                AudioState.Ready(exoPlayer.duration)

            Player.STATE_ENDED -> {
                //TODO()
            }

            Player.STATE_IDLE -> {
                //TODO()
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            serviceScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    private suspend fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            startProgressUpdate()
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        _audioState.value = AudioState.Playing(isPlaying = true)
        _audioState.value = AudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
        while (true) {
            delay(500)
            _audioState.value = AudioState.Progress(exoPlayer.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _audioState.value = AudioState.Playing(isPlaying = false)
    }

}

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()
    //data object SelectedAudioChange : PlayerEvent()
    //data object Backward : PlayerEvent()
    data object SeekToPrev : PlayerEvent()
    data object SeekToNext : PlayerEvent()
    //data object Forward : PlayerEvent()
    data object SeekTo : PlayerEvent()
    data object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class AudioState {
    data object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Buffering(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class Error(val cause: String) : AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}