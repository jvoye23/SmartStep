package com.jvcodingsolutions.smartstep.features.step_counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
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
    private val profileStorage: ProfileStorage
): ViewModel() {

    private val _state = MutableStateFlow(StepCounterState())

    private var hasLoadedInitialData = false

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
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
                // Update state with profile ID
                _state.update { it.copy(profileId = profileInfo.id) }
                
                // Fetch track data reactively after profile info is retrieved
                trackRepository.getCurrentStepGoalFlow(
                    profileId = profileInfo.id,
                    date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                ).collect { stepGoal ->
                    if (stepGoal != null) {
                        _state.update { it.copy(dailyGoalSteps = stepGoal) }
                    }
                }
            }
        }
    }

    fun onAction(action: StepCounterAction) {
        when(action) {
            StepCounterAction.OnToDoClick -> {}
            StepCounterAction.TogglePermissionRequest -> { togglePermissionRequest() }
            StepCounterAction.ToggleBackgroundAccessBottomSheet -> { toggleBackgroundAccessBottomSheet() }
            StepCounterAction.ToggleEnableAccessManuallyBottomSheet -> { toggleEnableAccessManuallyBottomSheet() }
            StepCounterAction.OnToggleStepGoalBottomSheet -> { toggleStepGoalBottomSheet() }
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