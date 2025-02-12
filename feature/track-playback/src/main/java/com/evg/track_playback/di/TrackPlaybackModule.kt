package com.evg.track_playback.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.track_playback.data.repository.TrackPlaybackRepositoryImpl
import com.evg.track_playback.domain.repository.TrackPlaybackRepository
import com.evg.track_playback.presentation.notification.AudioNotificationManager
import com.evg.track_playback.presentation.service.AudioServiceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackPlaybackModule {

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes,
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context).setLiveTargetOffsetMs(5000))
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .setTrackSelector(DefaultTrackSelector(context))
        .build()


    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession = MediaSession.Builder(context, player).build()

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): AudioNotificationManager = AudioNotificationManager(
        context = context,
        exoPlayer = player
    )

    @Provides
    @Singleton
    fun provideServiceHandler(exoPlayer: ExoPlayer): AudioServiceHandler =
        AudioServiceHandler(exoPlayer)


    @Provides
    @Singleton
    fun provideTrackPlaybackRepositoryModule(
        apiRepository: DeezerApiRepository,
    ): TrackPlaybackRepository {
        return TrackPlaybackRepositoryImpl(
            apiRepository = apiRepository,
        )
    }
}