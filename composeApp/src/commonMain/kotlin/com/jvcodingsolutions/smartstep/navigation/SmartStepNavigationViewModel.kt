package com.jvcodingsolutions.smartstep.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SmartStepNavigationViewModel(
    private val trackRepository: TrackRepository,
    private val profileStorage: ProfileStorage,
    private val stepTracker: StepTracker
): ViewModel() {
    private val _state = MutableStateFlow(SmartStepNavigationUiState())
    val state = _state.asStateFlow()

    fun onAction(action: SmartStepNavigationAction) {
        when (action) {
            SmartStepNavigationAction.OnToggleStepGoalBottomSheet -> { toggleStepGoalDialog() }
            SmartStepNavigationAction.ShowExitDialog -> { showExitDialog() }
            SmartStepNavigationAction.OnFixStopCountingIssueClick -> { /* Usually handled at UI level or sent to shared flow if needed */ }
            is SmartStepNavigationAction.OnSaveStepGoal -> { saveStepGoal(action.value) }
        }
    }

    private fun saveStepGoal(stepGoal: Int) {
        viewModelScope.launch {
            val profileInfo = profileStorage.get()
            if (profileInfo != null) {
                trackRepository.saveDailyStepGoal(
                    profileId = profileInfo.id,
                    date = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    stepGoal = stepGoal
                )
            }
            
            _state.update { it.copy(
                isStepGoalDialogVisible = false
            ) }
        }
    }

    private fun toggleStepGoalDialog() {
        _state.update { it.copy(
            isStepGoalDialogVisible = !_state.value.isStepGoalDialogVisible
        ) }
    }

    private fun showExitDialog() {
        _state.update { it.copy(
            isExitDialogVisible = true
        ) }
    }
}