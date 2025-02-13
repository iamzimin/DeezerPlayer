package com.evg.database.data.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.evg.database.domain.models.TracksDBO

@Dao
interface TracksDao {
    @Query("SELECT * FROM TracksDBO")
    suspend fun getAllTracks(): List<TracksDBO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TracksDBO)

    @Query("DELETE FROM TracksDBO WHERE trackId = :id")
    suspend fun removeTrackById(id: Long)

    @Query("SELECT * FROM TracksDBO WHERE trackId = :id LIMIT 1")
    suspend fun getTrackById(id: Long): TracksDBO?
}