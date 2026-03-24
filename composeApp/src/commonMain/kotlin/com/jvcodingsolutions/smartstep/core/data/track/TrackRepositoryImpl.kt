package com.jvcodingsolutions.smartstep.core.data.track

import com.jvcodingsolutions.smartstep.core.database.dao.TrackDao
import com.jvcodingsolutions.smartstep.core.database.entity.TrackEntity
import com.jvcodingsolutions.smartstep.core.database.mapper.toDomainModel
import com.jvcodingsolutions.smartstep.core.database.mapper.toEntity
import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.time.Duration

class TrackRepositoryImpl(
    private val trackDao: TrackDao
) : TrackRepository {

    override suspend fun insertTrack(track: Tracks) {
        trackDao.insertTrack(track.toEntity())
    }

    override suspend fun saveDailyStepGoal(profileId: String, date: LocalDate, stepGoal: Int) {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)
        
        if (existingTrack != null) {
            trackDao.insertTrack(existingTrack.copy(dailyStepGoal = stepGoal))
        } else {
            trackDao.insertTrack(
                TrackEntity(
                    profileId = profileId,
                    dailyStepGoal = stepGoal,
                    currentSteps = 0,
                    calories = null,
                    minutesMillis = null,
                    currentDate = epochDay
                )
            )
        }
    }

    override suspend fun saveCurrentSteps(profileId: String, date: LocalDate, currentSteps: Int) {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)

        if (existingTrack != null) {
            trackDao.insertTrack(existingTrack.copy(currentSteps = currentSteps))
        } else {
            trackDao.insertTrack(
                TrackEntity(
                    profileId = profileId,
                    dailyStepGoal = 6000, // Default or fetch from settings
                    currentSteps = currentSteps,
                    calories = null,
                    minutesMillis = null,
                    currentDate = epochDay
                )
            )
        }
    }

    override suspend fun getCurrentStepGoal(profileId: String, date: LocalDate): Int? {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)
        return existingTrack?.dailyStepGoal
    }

    override suspend fun getCurrentSteps(profileId: String, date: LocalDate): Int? {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)
        return existingTrack?.currentSteps
    }

    override fun getCurrentStepGoalFlow(profileId: String, date: LocalDate): Flow<Int?> {
        val epochDay = date.toEpochDays()
        return trackDao.getTrackByDateFlow(profileId, epochDay).map { it?.dailyStepGoal }
    }

    override fun getTracksForWeek(
        profileId: String,
        startOfWeek: LocalDate, // Assume this is a Monday
        endOfWeek: LocalDate   // Assume this is a Sunday
    ): Flow<List<Tracks>> {
        return trackDao.getTracksForPeriod(
            profileId = profileId,
            startEpochDay = startOfWeek.toEpochDays(),
            endEpochDay = endOfWeek.toEpochDays()
        ).map { entities ->
            val trackMap = entities.associateBy { it.currentDate }
            
            // Map into a full week list from Monday to Sunday
            val weekTracks = mutableListOf<Tracks>()
            var currentDay = startOfWeek
            
            for (i in 0..6) {
                val epochDay = currentDay.toEpochDays()
                val entity = trackMap[epochDay]
                
                if (entity != null) {
                    weekTracks.add(entity.toDomainModel())
                } else {
                    weekTracks.add(
                        Tracks(
                            profileId = profileId,
                            dailyStepGoal = 0,
                            currentSteps = 0,
                            calories = null,
                            minutes = null,
                            currentDate = currentDay
                        )
                    )
                }
                currentDay = currentDay.plus(1, DateTimeUnit.DAY)
            }
            weekTracks
        }
    }
}
