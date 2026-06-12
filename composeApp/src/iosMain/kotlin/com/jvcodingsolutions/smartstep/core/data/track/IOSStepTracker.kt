package com.jvcodingsolutions.smartstep.core.data.track

import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.CoreMotion.CMPedometer
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.NSOperationQueue

import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.darwin.NSObject

import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class IOSStepTracker(
    // Lazy to break the DI cycle: TrackRepositoryImpl also depends on StepTracker
    lazyTrackRepository: Lazy<com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository>,
    private val profileStorage: com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
) : StepTracker {
    private val trackRepository by lazyTrackRepository
    private val pedometer = CMPedometer()
    private val _stepDeltas = MutableSharedFlow<Int>(extraBufferCapacity = 64)
    override val stepDeltas: Flow<Int> = _stepDeltas.asSharedFlow()

    private val _currentTotalSteps = MutableStateFlow(0)
    override val currentTotalSteps: Flow<Int> = _currentTotalSteps.asStateFlow()

    private var lastStepCount = 0
    private val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main + kotlinx.coroutines.SupervisorJob())

    init {
        setupBackgroundObservers()
    }

    private fun setupBackgroundObservers() {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidEnterBackgroundNotification,
            `object` = null,
            queue = null
        ) { _ ->
            startLiveActivity()
        }

        NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationWillEnterForegroundNotification,
            `object` = null,
            queue = null
        ) { _ ->
            // endLiveActivity() if needed
        }
    }

    private fun startLiveActivity() {
        scope.launch {
            val profile = profileStorage.get() ?: return@launch
            val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            // Get current stats to initialize Live Activity
            val steps = trackRepository.getCurrentSteps(profile.id, today) ?: 0
            val goal = trackRepository.getCurrentStepGoal(profile.id, today) ?: 6000
            val calories = com.jvcodingsolutions.smartstep.core.presentation.util.calculateCalories(steps, profile)

            // Here we would call the Swift LiveActivityManager.shared.startLiveActivity
            // through an @objc bridge or by exposing the Swift class to Kotlin
        }
    }

    override fun startTracking() {
        if (!CMPedometer.isStepCountingAvailable()) return

        pedometer.startPedometerUpdatesFromDate(NSDate()) { data, error ->
            if (error == null && data != null) {
                val currentCount = data.numberOfSteps.intValue
                val delta = currentCount - lastStepCount
                if (delta > 0) {
                    lastStepCount = currentCount
                    _currentTotalSteps.value = currentCount
                    _stepDeltas.tryEmit(delta)
                }
            }
        }
    }

    override fun stopTracking() {
        pedometer.stopPedometerUpdates()
        lastStepCount = 0
    }
}
