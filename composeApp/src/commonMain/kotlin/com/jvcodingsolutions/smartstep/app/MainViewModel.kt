package com.jvcodingsolutions.smartstep.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.presentation.util.calculateCalories
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds

class MainViewModel(
    private val profileStorage: ProfileStorage,
    private val trackRepository: TrackRepository
): ViewModel() {

    private val _state = MutableStateFlow(MainState())
    private var hasLoadedInitialData = false

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                val profile = profileStorage.get()
                _state.update { it.copy(
                    isProfileSet = profile != null
                ) }
                
                if (profile != null) {
                    val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    viewModelScope.launch {
                        combine(
                            trackRepository.getCurrentStepsFlow(profile.id, today),
                            trackRepository.getCurrentStepGoalFlow(profile.id, today)
                        ) { steps, goal ->
                            val currentSteps = steps ?: 0
                            val currentGoal = goal ?: 6000
                            val calories = calculateCalories(currentSteps, profile)
                            
                            _state.update { it.copy(
                                currentSteps = currentSteps,
                                dailyGoal = currentGoal,
                                calories = calories
                            ) }
                        }.collect {}
                    }
                }

                _state.update { it.copy(
                    isCheckingProfile = false
                ) }
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )


}