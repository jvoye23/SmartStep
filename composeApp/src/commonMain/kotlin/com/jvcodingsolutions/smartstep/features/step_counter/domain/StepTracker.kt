package com.jvcodingsolutions.smartstep.features.step_counter.domain

import kotlinx.coroutines.flow.Flow

interface StepTracker {
    val stepDeltas: Flow<Int>
    val currentTotalSteps: Flow<Int>
    fun startTracking()
    fun stopTracking()
}
