package com.evg.track_playback.presentation.service


import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class AudioServiceHandler @OptIn(UnstableApi::class) @Inject constructor(
    private val context: Context,
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
                _audioState.value = AudioState.PlayError(cause = error.cause?.localizedMessage ?: "unknown") //TODO
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
            PlayerEvent.DownloadCurrentTrack -> downloadTrack()
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

    @OptIn(UnstableApi::class)
    private fun downloadTrack() {
        val currentMedia = exoPlayer.currentMediaItem ?: return

        val downloadHelper = DownloadHelper.forMediaItem(
            context,
            currentMedia,
            DefaultRenderersFactory(context),
            DefaultHttpDataSource.Factory()
        )

        downloadHelper.prepare(object : DownloadHelper.Callback {
            override fun onPrepared(helper: DownloadHelper) {
                val downloadRequest = DownloadRequest.Builder(
                    currentMedia.mediaId,
                    currentMedia.localConfiguration!!.uri,
                )
                    .setMimeType(currentMedia.localConfiguration!!.mimeType)
                    .setData(currentMedia.mediaMetadata.toString().toByteArray())
                    .build()

                DownloadService.sendAddDownload(
                    context,
                    AudioDownloadService::class.java,
                    downloadRequest,
                    true,
                )
            }

            override fun onPrepareError(helper: DownloadHelper, e: IOException) {}
        })
    }
}

sealed class PlayerEvent {
    data object DownloadCurrentTrack : PlayerEvent()
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
    data class PlayError(val cause: String) : AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}