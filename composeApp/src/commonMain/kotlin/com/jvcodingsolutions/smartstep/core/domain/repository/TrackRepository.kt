package com.jvcodingsolutions.smartstep.core.domain.repository

import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.time.Duration

interface TrackRepository {
    suspend fun insertTrack(track: Tracks)
    suspend fun saveDailyStepGoal(profileId: String, date: LocalDate, stepGoal: Int)
    suspend fun saveCurrentSteps(profileId: String, date: LocalDate, currentSteps: Int)
    suspend fun saveActivityDuration(profileId: String, date: LocalDate, duration: Duration)
    suspend fun saveSensorBaseline(profileId: String, date: LocalDate, baseline: Int)
    suspend fun resetDailySteps(profileId: String, date: LocalDate, newBaseline: Int)
    suspend fun getCurrentStepGoal(profileId: String, date: LocalDate): Int?
    suspend fun getCurrentSteps(profileId: String, date: LocalDate): Int?
    suspend fun getCurrentActivityDurationInMinutes(profileId: String, date: LocalDate): Duration?
    fun getCurrentStepsFlow(profileId: String, date: LocalDate): Flow<Int?>
    fun getCurrentStepGoalFlow(profileId: String, date: LocalDate): Flow<Int?>
    fun getTracksForWeek(profileId: String, startOfWeek: LocalDate, endOfWeek: LocalDate): Flow<List<Tracks>>

    fun getWeeklyTracksFlow(
        profileId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Tracks>>

    // New: Unified live track data flow to keep App and Service in perfect sync
    fun getLiveStepsFlow(profileId: String, date: LocalDate): Flow<Int>
    fun getLiveDurationFlow(profileId: String, date: LocalDate): Flow<Duration>
    fun addStepDelta(delta: Int)
    fun addDurationDelta(delta: Duration)
}
