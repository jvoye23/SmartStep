package com.jvcodingsolutions.smartstep.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jvcodingsolutions.smartstep.core.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE profileId = :profileId AND currentDate = :epochDay")
    suspend fun getTrackByDate(profileId: String, epochDay: Long): TrackEntity?

    @Query("SELECT * FROM tracks WHERE profileId = :profileId AND currentDate = :epochDay")
    fun getTrackByDateFlow(profileId: String, epochDay: Long): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE profileId = :profileId AND currentDate BETWEEN :startEpochDay AND :endEpochDay ORDER BY currentDate ASC")
    fun getTracksForPeriod(profileId: String, startEpochDay: Long, endEpochDay: Long): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE profileId = :profileId AND currentDate >= :startEpochDay AND currentDate <= :endEpochDay  ORDER BY currentDate ASC ")
    fun getTracksForDateRangeFlow( profileId: String, startEpochDay: Long, endEpochDay: Long): Flow<List<TrackEntity>>


}