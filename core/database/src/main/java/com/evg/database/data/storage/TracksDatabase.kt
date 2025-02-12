package com.evg.database.data.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.evg.database.domain.models.TracksDBO

@Database(
    entities = [TracksDBO::class],
    version = 1,
)
abstract class TracksDatabase: RoomDatabase() {
    abstract val tracksDao: TracksDao

    companion object {
        const val DATABASE_NAME = "tracks"
    }
}