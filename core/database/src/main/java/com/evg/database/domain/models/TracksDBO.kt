package com.evg.database.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TracksDBO(
    @PrimaryKey val trackId: Long,
    val trackTitle: String,
    val trackPreview: String,
    val artistName: String,
    val albumID: Long,
    val albumCover: String,
)