package com.evg.track_playback.presentation.service

import android.app.Notification
import androidx.annotation.StringRes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import com.evg.resource.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class AudioDownloadService : DownloadService(
    NOTIFICATION_ID, // foregroundNotificationId
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL, // foregroundNotificationUpdateInterval
    NOTIFICATION_CHANNEL_ID, // channelId
    NOTIFICATION_CHANNEL_NAME_RES, // channelNameResourceId
    NOTIFICATION_CHANNEL_NAME_RES, // channelDescriptionResourceId
) {
    companion object {
        const val NOTIFICATION_ID = 102
        const val NOTIFICATION_CHANNEL_ID = "track_download"
        @StringRes val NOTIFICATION_CHANNEL_NAME_RES = R.string.track_download
    }

    @Inject
    lateinit var downloadManagerInstance: DownloadManager

    @Inject
    lateinit var notification: Notification

    override fun getDownloadManager(): DownloadManager = downloadManagerInstance

    override fun getScheduler(): Scheduler? = null

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification = notification
}