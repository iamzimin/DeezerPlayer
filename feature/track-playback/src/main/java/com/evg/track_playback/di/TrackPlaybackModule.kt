package com.evg.track_playback.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.database.domain.repository.DatabaseRepository
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
        cache: Cache,
        upstreamDataSourceFactory: DataSource.Factory,
    ): ExoPlayer {
        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
                .setCacheWriteDataSinkFactory(null)

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setLiveTargetOffsetMs(5000)
                    .setDataSourceFactory(cacheDataSourceFactory)
            )
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(DefaultTrackSelector(context))
            .build()
    }


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

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideServiceHandler(
        @ApplicationContext context: Context,
        exoPlayer: ExoPlayer,
    ): AudioServiceHandler {
        return AudioServiceHandler(
            context = context,
            exoPlayer = exoPlayer,
        )
    }


    @Provides
    @Singleton
    fun provideTrackPlaybackRepositoryModule(
        apiRepository: DeezerApiRepository,
        databaseRepository: DatabaseRepository,
    ): TrackPlaybackRepository {
        return TrackPlaybackRepositoryImpl(
            apiRepository = apiRepository,
            databaseRepository = databaseRepository,
        )
    }
}