package com.jvcodingsolutions.smartstep.features.step_counter

import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

data class StepCounterState(
    val profileId: String = "",
    val isProfileMetricSystem: Boolean = true,
    val currentSteps: Int = 0,
    val dailyGoalSteps: Int = 6000,
    val isPermissionRequested: Boolean = false,
    val isAllowAccessBottomSheetVisible: Boolean = false,
    val isEnableAccessManuallyBottomSheetVisible: Boolean = false,
    val isBackgroundAccessBottomSheetVisible: Boolean = true,
    val isStepGoalBottomSheetVisible: Boolean = false,
    val currentSelectedStepGoalValue: Int = 2000,
    val isEditStepsDialogVisible: Boolean = false,
    val isDatePickerDialogVisible: Boolean = false,
    val editedDate: LocalDate = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val isResetStepsConfirmationDialogVisible: Boolean = false,
    val dailyAverageState: DailyAverageState = DailyAverageState(dailyAverage = 0, dailySteps = emptyList()),
    val distanceTraveled: String = "0.0",
    val caloriesBurned: Int = 0,
    val activityDuration: String = "0 min",
    val activityDurationRaw: Duration = Duration.ZERO,
    val isStepTrackerPaused: Boolean = false
)

data class DailyAverageState(
    val dailyAverage: Int = 0,
    val dailySteps: List<DayStepData> = emptyList()
)

data class DayStepData(
    val dayName: String,
    val steps: Int,
    val progress: Float
)

fun mapTracksToDailyAverageState(
    tracks: List<Tracks>,
    today: LocalDate,
    todayCurrentSteps: Int, // <-- Pass the real-time truth here
    fallbackGoal: Int = 6000
): DailyAverageState {
    val tracksMap = tracks.associateBy { it.currentDate }

    var totalSteps = 0
    val dayStepDataList = mutableListOf<DayStepData>()

    for (i in 6 downTo 0) {
        val date = LocalDate.fromEpochDays(today.toEpochDays() - i)
        val track = tracksMap[date]

        // CRITICAL: If the date in the loop is today, use the live UI state.
        // Otherwise, use the historical database value.
        val steps = if (date == today) {
            todayCurrentSteps
        } else {
            track?.currentSteps ?: 0
        }

        val goal = track?.dailyStepGoal ?: fallbackGoal

        val progress = if (goal > 0) {
            (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        val dayName = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

        dayStepDataList.add(DayStepData(dayName, steps, progress))
        totalSteps += steps
    }

    val dailyAverage = totalSteps / 7

    return DailyAverageState(
        dailyAverage = dailyAverage,
        dailySteps = dayStepDataList
    )
}
