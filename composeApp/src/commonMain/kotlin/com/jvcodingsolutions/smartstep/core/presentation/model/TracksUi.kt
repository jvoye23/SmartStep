package com.jvcodingsolutions.smartstep.core.presentation.model

import com.jvcodingsolutions.smartstep.core.domain.model.Tracks

data class TracksUi(
    val currentSteps: String,
    val dailyStepGoal: String,
    val calories: String,
    val minutes: String,
    val dayOfWeek: String, // e.g., "Mon", "Tue"
    val date: String // Optional: "yyyy-MM-dd"
)

fun Tracks.toUiModel(): TracksUi {
    return TracksUi(
        currentSteps = currentSteps.toString(),
        dailyStepGoal = dailyStepGoal.toString(),
        calories = calories?.toString() ?: "0",
        minutes = (minutes?.inWholeMinutes ?: 0L).toString() + " min",
        dayOfWeek = currentDate.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
        date = currentDate.toString()
    )
}
