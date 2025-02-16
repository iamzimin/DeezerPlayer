package com.evg.track_playback.presentation.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.imageLoader
import coil.request.ImageRequest
import com.evg.resource.R

/**
 * Адаптер для уведомлений аудиоплеера
 *
 * @property context Контекст приложения
 * @property pendingIntent Интент для обработки кликов по уведомлению
 */
@UnstableApi
class AudioNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?,
) : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: context.getString(R.string.unknown)

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.artist ?: context.getString(R.string.unknown)

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback,
    ): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(player.mediaMetadata.artworkUri)
            .target(
                onStart = {},
                onError = {},
                onSuccess = { drawable ->
                    val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: drawableToBitmap(drawable)
                    callback.onBitmap(bitmap)
                }
            )
            .build()
        context.imageLoader.enqueue(request)
        return null
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.takeIf { it > 0 } ?: 1,
            drawable.intrinsicHeight.takeIf { it > 0 } ?: 1,
            Bitmap.Config.ARGB_8888
        )
        Canvas(bitmap).also {
            drawable.setBounds(0, 0, it.width, it.height)
            drawable.draw(it)
        }
        return bitmap
    }
}