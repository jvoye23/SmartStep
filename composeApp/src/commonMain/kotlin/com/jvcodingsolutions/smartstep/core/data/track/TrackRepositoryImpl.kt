package com.jvcodingsolutions.smartstep.core.data.track

import com.jvcodingsolutions.smartstep.core.database.dao.TrackDao
import com.jvcodingsolutions.smartstep.core.database.entity.TrackEntity
import com.jvcodingsolutions.smartstep.core.database.mapper.toDomainModel
import com.jvcodingsolutions.smartstep.core.database.mapper.toEntity
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import com.jvcodingsolutions.smartstep.core.presentation.util.calculateActiveTimeDelta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TrackRepositoryImpl(
    private val trackDao: TrackDao,
    private val profileStorage: ProfileStorage,
    private val stepTracker: StepTracker,
    private val repositoryScope: CoroutineScope
) : TrackRepository {

    private val _unwrittenSteps = MutableStateFlow(0)
    private val _unwrittenDuration = MutableStateFlow(Duration.ZERO)
    
    private var lastStepDetectionTime: Long? = null

    init {
        repositoryScope.launch {
            stepTracker.stepDeltas.collect { delta ->
                val currentTime = kotlin.time.Clock.System.now().toEpochMilliseconds()
                val timeSinceLastStep = lastStepDetectionTime?.let {
                    (currentTime - it).milliseconds
                }
                lastStepDetectionTime = currentTime
                val activeTimeDelta = calculateActiveTimeDelta(timeSinceLastStep)

                addStepDelta(delta)
                if (activeTimeDelta > Duration.ZERO) {
                    addDurationDelta(activeTimeDelta)
                }

                // Auto-save logic
                if (_unwrittenSteps.value >= 10) {
                    savePendingData()
                }
            }
        }
    }

    private suspend fun savePendingData() {
        val profile = profileStorage.get() ?: return
        val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        val existingTrack = trackDao.getTrackByDate(profile.id, today.toEpochDays())
        val currentTotalSteps = (existingTrack?.currentSteps ?: 0) + _unwrittenSteps.value
        val currentTotalDuration = (existingTrack?.minutesMillis?.milliseconds ?: Duration.ZERO) + _unwrittenDuration.value

        saveCurrentSteps(profile.id, today, currentTotalSteps)
        saveActivityDuration(profile.id, today, currentTotalDuration)
    }

    override fun addStepDelta(delta: Int) {
        if (delta == 0) return
        _unwrittenSteps.update { it + delta }
    }

    override fun addDurationDelta(delta: Duration) {
        if (delta <= Duration.ZERO) return
        _unwrittenDuration.update { it + delta }
    }

    override fun getLiveStepsFlow(profileId: String, date: LocalDate): Flow<Int> {
        val epochDay = date.toEpochDays()
        return trackDao.getTrackByDateFlow(profileId, epochDay)
            .combine(_unwrittenSteps) { entity, unwritten ->
                (entity?.currentSteps ?: 0) + unwritten
            }
            .distinctUntilChanged()
    }

    override fun getLiveDurationFlow(profileId: String, date: LocalDate): Flow<Duration> {
        val epochDay = date.toEpochDays()
        return trackDao.getTrackByDateFlow(profileId, epochDay)
            .combine(_unwrittenDuration) { entity, unwritten ->
                (entity?.minutesMillis?.milliseconds ?: Duration.ZERO) + unwritten
            }
            .distinctUntilChanged()
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
                    dailyStepGoal = 6000, 
                    currentSteps = currentSteps,
                    sensorBaseline = 0,
                    calories = null,
                    minutesMillis = null,
                    currentDate = epochDay
                )
            )
        }
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
        return existingTrack?.minutesMillis?.milliseconds
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
        startOfWeek: LocalDate, 
        endOfWeek: LocalDate   
    ): Flow<List<Tracks>> {
        return trackDao.getTracksForPeriod(
            profileId = profileId,
            startEpochDay = startOfWeek.toEpochDays(),
            endEpochDay = endOfWeek.toEpochDays()
        ).map { entities ->
            val trackMap = entities.associateBy { it.currentDate }
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
                entities.map { it.toDomainModel() }
            }
    }
}
