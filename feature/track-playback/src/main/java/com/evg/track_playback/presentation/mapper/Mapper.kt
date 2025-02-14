package com.evg.track_playback.presentation.mapper

import android.content.Context
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackException.ERROR_CODE_AUDIO_TRACK_INIT_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_AUDIO_TRACK_OFFLOAD_INIT_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_AUDIO_TRACK_OFFLOAD_WRITE_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_AUDIO_TRACK_WRITE_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_BAD_VALUE
import androidx.media3.common.PlaybackException.ERROR_CODE_DECODER_INIT_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_DECODER_QUERY_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_DECODING_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES
import androidx.media3.common.PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED
import androidx.media3.common.PlaybackException.ERROR_CODE_DISCONNECTED
import androidx.media3.common.PlaybackException.ERROR_CODE_INVALID_STATE
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_NO_PERMISSION
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE
import androidx.media3.common.PlaybackException.ERROR_CODE_IO_UNSPECIFIED
import androidx.media3.common.PlaybackException.ERROR_CODE_NOT_SUPPORTED
import androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED
import androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED
import androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED
import androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED
import androidx.media3.common.PlaybackException.ERROR_CODE_PERMISSION_DENIED
import androidx.media3.common.PlaybackException.ERROR_CODE_TIMEOUT
import androidx.media3.common.PlaybackException.ERROR_CODE_UNSPECIFIED
import com.evg.resource.R
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Locale

fun Long.toFormattedTimeWithProgress(progress: Float): String {
    val currentPositionMs = (progress / 100f * this).toLong()
    val minutes = (currentPositionMs / 1000) / 60
    val seconds = (currentPositionMs / 1000) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

fun Long.toFormattedTime(): String {
    val minutes = (this / 1000) / 60
    val seconds = (this / 1000) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

fun getLocalizedExceptionMessage(context: Context, exception: Exception?): String {
    return when (exception) {
        is HttpException -> {
            when (exception.code()) {
                403 -> context.getString(R.string.error_forbidden)
                404 -> context.getString(R.string.error_not_found)
                408 -> context.getString(R.string.error_request_timeout)
                429 -> context.getString(R.string.error_too_many_requests)
                in 500..599 -> context.getString(R.string.error_server)
                else -> exception.localizedMessage
            }
        }
        is SocketTimeoutException -> context.getString(R.string.error_request_timeout)
        is UnknownHostException -> context.getString(R.string.error_unknown_host)
        else -> exception?.localizedMessage ?: context.getString(R.string.error_unknown)
    }
}

fun getLocalizedPlayback(context: Context, playbackException: PlaybackException): String {
    val stringResId = when (playbackException.errorCode) {
        ERROR_CODE_INVALID_STATE -> R.string.error_invalid_state
        ERROR_CODE_BAD_VALUE -> R.string.error_bad_value
        ERROR_CODE_PERMISSION_DENIED -> R.string.error_permission_denied
        ERROR_CODE_NOT_SUPPORTED -> R.string.error_not_supported
        ERROR_CODE_DISCONNECTED -> R.string.error_disconnected
        ERROR_CODE_UNSPECIFIED -> R.string.error_unspecified
        ERROR_CODE_TIMEOUT -> R.string.error_request_timeout
        ERROR_CODE_IO_UNSPECIFIED -> R.string.error_io_unspecified
        ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> R.string.error_connection
        ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> R.string.error_network_connection_timeout
        ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE -> R.string.error_invalid_http_content_type
        ERROR_CODE_IO_BAD_HTTP_STATUS -> R.string.error_bad_http_status
        ERROR_CODE_IO_FILE_NOT_FOUND -> R.string.error_file_not_found
        ERROR_CODE_IO_NO_PERMISSION -> R.string.error_no_permission
        ERROR_CODE_IO_READ_POSITION_OUT_OF_RANGE -> R.string.error_read_position_out_of_range
        ERROR_CODE_PARSING_CONTAINER_MALFORMED -> R.string.error_parsing_container_malformed
        ERROR_CODE_PARSING_MANIFEST_MALFORMED -> R.string.error_parsing_manifest_malformed
        ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> R.string.error_parsing_container_unsupported
        ERROR_CODE_PARSING_MANIFEST_UNSUPPORTED -> R.string.error_parsing_manifest_unsupported
        ERROR_CODE_DECODER_INIT_FAILED -> R.string.error_decoder_init_failed
        ERROR_CODE_DECODER_QUERY_FAILED -> R.string.error_decoder_query_failed
        ERROR_CODE_DECODING_FAILED -> R.string.error_decoding_failed
        ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES -> R.string.error_decoding_format_exceeds_capabilities
        ERROR_CODE_DECODING_FORMAT_UNSUPPORTED -> R.string.error_decoding_format_unsupported
        ERROR_CODE_AUDIO_TRACK_INIT_FAILED -> R.string.error_audio_track_init_failed
        ERROR_CODE_AUDIO_TRACK_WRITE_FAILED -> R.string.error_audio_track_write_failed
        ERROR_CODE_AUDIO_TRACK_OFFLOAD_WRITE_FAILED -> R.string.error_audio_track_offload_write_failed
        ERROR_CODE_AUDIO_TRACK_OFFLOAD_INIT_FAILED -> R.string.error_audio_track_offload_init_failed
        else -> null
    }
    return if (stringResId == null) {
        playbackException.localizedMessage ?: context.getString(R.string.error_unknown)
    } else {
        context.getString(stringResId)
    }
}
