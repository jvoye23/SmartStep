package com.jvcodingsolutions.smartstep.features.ai_coach.domain

data class ActivityContext(
    val currentSteps: Int,
    val dailyStepGoal: Int,
    val goalCompletionPercent: Int,
    val timeOfDay: TimeOfDay
) {
    companion object {
        fun create(currentSteps: Int, dailyStepGoal: Int, hourOfDay: Int): ActivityContext {
            val percent = if (dailyStepGoal > 0) {
                (currentSteps * 100) / dailyStepGoal
            } else 0
            return ActivityContext(
                currentSteps = currentSteps,
                dailyStepGoal = dailyStepGoal,
                goalCompletionPercent = percent,
                timeOfDay = TimeOfDay.fromHour(hourOfDay)
            )
        }
    }
}

enum class TimeOfDay {
    MORNING,
    DAY,
    EVENING;

    companion object {
        fun fromHour(hour: Int): TimeOfDay = when (hour) {
            in 5..11 -> MORNING
            in 12..17 -> DAY
            else -> EVENING
        }
    }
}
