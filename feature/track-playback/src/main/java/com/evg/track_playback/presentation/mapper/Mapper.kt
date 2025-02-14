package com.evg.track_playback.presentation.mapper

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