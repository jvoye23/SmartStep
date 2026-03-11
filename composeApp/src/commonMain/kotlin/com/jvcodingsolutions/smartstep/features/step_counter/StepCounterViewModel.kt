package com.jvcodingsolutions.smartstep.features.step_counter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StepCounterViewModel: ViewModel() {

    private val _state = MutableStateFlow(StepCounterState())
    val state = _state.asStateFlow()

    fun onAction(action: StepCounterAction) {
        when(action) {
            StepCounterAction.OnToDoClick -> {}
            StepCounterAction.TogglePermissionRequest -> { togglePermissionRequest() }
            StepCounterAction.ToggleAllowAccessBottomSheet -> { toggleEnableAccessManuallyBottomSheet() }
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
        _state.update { it.copy(
            currentSelectedStepGoalValue = value,
            dailyGoalSteps = value
        ) }
    }

    fun togglePermissionRequest() {
        _state.update { it.copy(
            isPermissionRequested = !_state.value.isPermissionRequested
        ) }
    }

    private fun toggleAllowAccessBottomSheet() {
        _state.update {
            it.copy(
                isAllowAccessBottomSheetVisible = !_state.value.isAllowAccessBottomSheetVisible
            )
        }
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