package com.jvcodingsolutions.smartstep.core.data.track

import com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class IOSStepTracker : StepTracker {
    // Basic stub for iOS. Apple's CMPedometer can be used here later.
    override val stepDeltas: Flow<Int> = emptyFlow()

    override fun startTracking() {
        // Implement CMPedometer logic for iOS later
    }

    override fun stopTracking() {
        // Stop CMPedometer logic
    }
}
