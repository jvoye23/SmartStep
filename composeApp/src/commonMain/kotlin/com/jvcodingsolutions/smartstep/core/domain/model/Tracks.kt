package com.jvcodingsolutions.smartstep.core.domain.model

import kotlinx.datetime.LocalDate
import kotlin.time.Duration

data class Tracks(
    val id: Long = 0,
    val profileId: String,
    val dailyStepGoal: Int,
    val currentSteps: Int = 0,
    val calories: Int? = null,
    val minutes: Duration? = null,
    val currentDate: LocalDate
)
