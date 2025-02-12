package com.evg.track_playback.di

import android.app.Notification
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.offline.DownloadManager
import com.evg.resource.R
import com.evg.track_playback.presentation.service.AudioDownloadService.Companion.NOTIFICATION_CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrackDownloadModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadNotification(
        @ApplicationContext context: Context
    ): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Downloading audio")
            .setSmallIcon(R.drawable.sad)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDatabaseProvider(
        @ApplicationContext context: Context
    ): DatabaseProvider {
        return StandaloneDatabaseProvider(context)
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        return SimpleCache(
            File(context.cacheDir, "mediaCache"),
            NoOpCacheEvictor(),
            StandaloneDatabaseProvider(context)
        )
    }

    @Provides
    @Singleton
    fun provideUpstreamDataSourceFactory(): DataSource.Factory {
        return OkHttpDataSource.Factory(OkHttpClient())
    }

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        databaseProvider: DatabaseProvider,
        cache: Cache,
        upstreamDataSourceFactory: DataSource.Factory,
    ): DownloadManager {
        val dataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)

        return DownloadManager(
            context,
            databaseProvider,
            cache,
            dataSourceFactory,
            Executors.newFixedThreadPool(4)
        ).apply {
            maxParallelDownloads = 3
        }
    }
}