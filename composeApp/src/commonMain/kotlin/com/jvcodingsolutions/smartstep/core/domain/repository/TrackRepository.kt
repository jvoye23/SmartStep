package com.jvcodingsolutions.smartstep.core.domain.repository

import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TrackRepository {
    suspend fun insertTrack(track: Tracks)
    suspend fun saveDailyStepGoal(profileId: String, date: LocalDate, stepGoal: Int)
    suspend fun getCurrentStepGoal(profileId: String, date: LocalDate): Int?
    fun getCurrentStepGoalFlow(profileId: String, date: LocalDate): Flow<Int?>
    fun getTracksForWeek(profileId: String, startOfWeek: LocalDate, endOfWeek: LocalDate): Flow<List<Tracks>>
}
