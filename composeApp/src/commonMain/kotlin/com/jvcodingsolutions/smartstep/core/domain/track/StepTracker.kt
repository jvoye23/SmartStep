package com.jvcodingsolutions.smartstep.core.domain.track

import kotlinx.coroutines.flow.Flow

interface StepTracker {
    val stepDeltas: Flow<Int>
    val currentTotalSteps: Flow<Int>
    fun startTracking()
    fun stopTracking()
}
