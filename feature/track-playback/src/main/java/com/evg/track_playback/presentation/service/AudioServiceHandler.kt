package com.evg.track_playback.presentation.service


import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadHelper
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import com.evg.track_playback.presentation.model.AudioState
import com.evg.track_playback.presentation.model.PlayerEvent
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
    ) {
        when (playerEvent) {
            PlayerEvent.DownloadCurrentTrack -> downloadTrack()
            is PlayerEvent.RemoveCurrentTrack -> removeTrack(playerEvent.isRemoveCurrentMedia)
            PlayerEvent.SeekToPrev -> exoPlayer.seekToPrevious()
            PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            PlayerEvent.Play -> play()
            PlayerEvent.PlayPause -> playOrPause()
            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.PlayByIndex -> {
                if (playerEvent.index != exoPlayer.currentMediaItemIndex) {
                    exoPlayer.seekToDefaultPosition(playerEvent.index)
                }
            }
            is PlayerEvent.SeekTo -> exoPlayer.seekTo(playerEvent.seekPosition)
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_READY -> _audioState.value =
                AudioState.Ready(exoPlayer.duration)
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

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        _audioState.value = AudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
    }


    private suspend fun playOrPause() {
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    private fun pause() {
        exoPlayer.pause()
        stopProgressUpdate()
    }

    private suspend fun play() {
        exoPlayer.play()
        startProgressUpdate()
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

    @OptIn(UnstableApi::class)
    private fun removeTrack(isRemoveCurrentMedia: Boolean) {
        val currentMedia = exoPlayer.currentMediaItem ?: return

        if (isRemoveCurrentMedia) {
            exoPlayer.removeMediaItem(exoPlayer.currentMediaItemIndex)
        }
        DownloadService.sendRemoveDownload(
            context,
            AudioDownloadService::class.java,
            currentMedia.mediaId,
            true
        )
    }
}
