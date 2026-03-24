package com.jvcodingsolutions.smartstep.features.step_counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class StepCounterViewModel(
    private val trackRepository: TrackRepository,
    private val profileStorage: ProfileStorage,
    private val stepTracker: StepTracker
): ViewModel() {

    private val _state = MutableStateFlow(StepCounterState())

    private var hasLoadedInitialData = false

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
            stepTracker.startTracking()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )

    private fun loadInitialData() {
        viewModelScope.launch {
            val profileInfo = profileStorage.get()
            if (profileInfo != null) {
                val profileId = profileInfo.id
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                
                _state.update { it.copy(profileId = profileId) }

                // Load initial current steps for today
                val initialSteps = trackRepository.getCurrentSteps(profileId, today) ?: 0
                _state.update { it.copy(currentSteps = initialSteps) }

                // Fetch step goal reactively
                launch {
                    trackRepository.getCurrentStepGoalFlow(profileId, today).collect { stepGoal ->
                        if (stepGoal != null) {
                            _state.update { it.copy(dailyGoalSteps = stepGoal) }
                        }
                    }
                }

                // Collect step deltas and debounce database saves
                launch {
                    var unwrittenSteps = 0
                    stepTracker.stepDeltas.collect { delta ->
                        unwrittenSteps += delta
                        val newTotal = _state.value.currentSteps + delta
                        
                        _state.update { it.copy(currentSteps = newTotal) }

                        // Write to DB periodically (e.g. every 10 steps)
                        if (unwrittenSteps >= 10) {
                            trackRepository.saveCurrentSteps(profileId, today, newTotal)
                            unwrittenSteps = 0
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Ensure final steps are saved before view model is destroyed
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState.profileId.isNotEmpty()) {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                trackRepository.saveCurrentSteps(currentState.profileId, today, currentState.currentSteps)
            }
        }
        stepTracker.stopTracking()
    }

    fun onAction(action: StepCounterAction) {
        when(action) {
            StepCounterAction.OnToDoClick -> {}
            StepCounterAction.TogglePermissionRequest -> { togglePermissionRequest() }
            StepCounterAction.ToggleBackgroundAccessBottomSheet -> { toggleBackgroundAccessBottomSheet() }
            StepCounterAction.ToggleEnableAccessManuallyBottomSheet -> { toggleEnableAccessManuallyBottomSheet() }
            StepCounterAction.OnToggleStepGoalBottomSheet -> { toggleStepGoalBottomSheet() }
            StepCounterAction.StartTracking -> { stepTracker.startTracking() }
            is StepCounterAction.OnSaveStepGoal -> { saveStepGoal(action.value) }
            else -> Unit
        }
    }

    private fun toggleStepGoalBottomSheet() {
        _state.update {
            it.copy(
                isStepGoalBottomSheetVisible = !_state.value.isStepGoalBottomSheetVisible
            )
        }
        println("State: ${_state.value}")
    }

    private fun saveStepGoal(value: Int) {
        viewModelScope.launch {
            trackRepository.saveDailyStepGoal(
                profileId = state.value.profileId,
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                stepGoal = value
            )
        }
    }

    fun togglePermissionRequest() {
        _state.update { it.copy(
            isPermissionRequested = !_state.value.isPermissionRequested
        ) }
    }

    private fun toggleEnableAccessManuallyBottomSheet() {
        _state.update {
            it.copy(
                isEnableAccessManuallyBottomSheetVisible = !_state.value.isEnableAccessManuallyBottomSheetVisible
            )
        }
    }

    private fun toggleBackgroundAccessBottomSheet() {
        _state.update {
            it.copy(
                isBackgroundAccessBottomSheetVisible = !_state.value.isBackgroundAccessBottomSheetVisible
            )
        }
    }
}