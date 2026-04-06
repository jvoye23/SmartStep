package com.jvcodingsolutions.smartstep.core.data.track

import com.jvcodingsolutions.smartstep.core.database.dao.TrackDao
import com.jvcodingsolutions.smartstep.core.database.entity.TrackEntity
import com.jvcodingsolutions.smartstep.core.database.mapper.toDomainModel
import com.jvcodingsolutions.smartstep.core.database.mapper.toEntity
import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class TrackRepositoryImpl(
    private val trackDao: TrackDao
) : TrackRepository {

    private val _unwrittenSteps = MutableStateFlow(0)
    private val _unwrittenDuration = MutableStateFlow(Duration.ZERO)
    
    override fun addStepDelta(delta: Int) {
        _unwrittenSteps.update { it + delta }
    }

    override fun addDurationDelta(delta: Duration) {
        _unwrittenDuration.update { it + delta }
    }

    override fun getLiveStepsFlow(profileId: String, date: LocalDate): Flow<Int> {
        val epochDay = date.toEpochDays()
        return trackDao.getTrackByDateFlow(profileId, epochDay)
            .distinctUntilChanged { old, new -> old?.currentSteps == new?.currentSteps }
            .combine(_unwrittenSteps) { entity, unwritten ->
                (entity?.currentSteps ?: 0) + unwritten
            }
    }

    override fun getLiveDurationFlow(profileId: String, date: LocalDate): Flow<Duration> {
        val epochDay = date.toEpochDays()
        return trackDao.getTrackByDateFlow(profileId, epochDay)
            .distinctUntilChanged { old, new -> old?.minutesMillis == new?.minutesMillis }
            .combine(_unwrittenDuration) { entity, unwritten ->
                (entity?.minutesMillis?.milliseconds ?: Duration.ZERO) + unwritten
            }
    }

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
                    sensorBaseline = 0,
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
                    sensorBaseline = 0,
                    calories = null,
                    minutesMillis = null,
                    currentDate = epochDay
                )
            )
        }
        // When steps are saved to DB, we must subtract them from the unwritten counter
        // However, in this app's architecture, we usually pass the 'newTotal' to this method.
        // To keep it simple and synchronized: the ViewModel or Service that calls this
        // should be the one to manage _unwrittenSteps. 
        // Or we can reset it here if we assume this is the authoritative 'write'
        _unwrittenSteps.update { 0 }
    }

    override suspend fun saveActivityDuration(profileId: String, date: LocalDate, duration: Duration) {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)

        if (existingTrack != null) {
            trackDao.insertTrack(existingTrack.copy(minutesMillis = duration.inWholeMilliseconds))
        } else {
            trackDao.insertTrack(
                TrackEntity(
                    profileId = profileId,
                    dailyStepGoal = 6000,
                    currentSteps = 0,
                    sensorBaseline = 0,
                    calories = null,
                    minutesMillis = duration.inWholeMilliseconds,
                    currentDate = epochDay
                )
            )
        }
        _unwrittenDuration.update { Duration.ZERO }
    }

    override suspend fun saveSensorBaseline(profileId: String, date: LocalDate, baseline: Int) {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)

        if (existingTrack != null) {
            trackDao.insertTrack(existingTrack.copy(sensorBaseline = baseline))
        } else {
            trackDao.insertTrack(
                TrackEntity(
                    profileId = profileId,
                    dailyStepGoal = 6000,
                    currentSteps = 0,
                    sensorBaseline = baseline,
                    calories = null,
                    minutesMillis = null,
                    currentDate = epochDay
                )
            )
        }
    }

    override suspend fun resetDailySteps(profileId: String, date: LocalDate, newBaseline: Int) {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)

        if (existingTrack != null) {
            trackDao.insertTrack(existingTrack.copy(currentSteps = 0, sensorBaseline = newBaseline))
        } else {
            trackDao.insertTrack(
                TrackEntity(
                    profileId = profileId,
                    dailyStepGoal = 6000,
                    currentSteps = 0,
                    sensorBaseline = newBaseline,
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

    override suspend fun getCurrentActivityDurationInMinutes(
        profileId: String,
        date: LocalDate
    ): Duration? {
        val epochDay = date.toEpochDays()
        val existingTrack = trackDao.getTrackByDate(profileId, epochDay)
        return existingTrack?.minutesMillis?.minutes
    }

    override fun getCurrentStepsFlow(profileId: String, date: LocalDate): Flow<Int?> {
        val epochDay = date.toEpochDays()
        return trackDao.getTrackByDateFlow(profileId, epochDay).map { it?.currentSteps }
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
                            sensorBaseline = 0,
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

    override fun getWeeklyTracksFlow(
        profileId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Tracks>> {
        return trackDao.getTracksForDateRangeFlow(profileId, startDate.toEpochDays(), endDate.toEpochDays())
            .map { entities ->
                // Assuming you have an extension function to map DB entity to Domain model
                entities.map { it.toDomainModel() }
            }
    }
}
