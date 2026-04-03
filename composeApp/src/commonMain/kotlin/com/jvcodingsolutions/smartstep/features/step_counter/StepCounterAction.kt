package com.jvcodingsolutions.smartstep.features.step_counter

import com.jvcodingsolutions.smartstep.navigation.SmartStepNavigationAction
import kotlinx.datetime.LocalDate

sealed interface StepCounterAction {
    data object OnNavigationMenuClick: StepCounterAction
    data object OnToDoClick: StepCounterAction
    data object TogglePermissionRequest: StepCounterAction
    data object ToggleAllowAccessBottomSheet: StepCounterAction
    data object ToggleEnableAccessManuallyBottomSheet: StepCounterAction
    data object ToggleBackgroundAccessBottomSheet: StepCounterAction
    data object OnAllowAccessButtonClick: StepCounterAction
    data object OnOpenSettingsClick: StepCounterAction
    data object OnContinueBackgroundAccessClick: StepCounterAction
    data object OnFixStopCountingIssueClick: StepCounterAction
    data object StartTracking: StepCounterAction

    data object OnToggleStepGoalBottomSheet: StepCounterAction

    data class OnSaveStepGoal(val value: Int): StepCounterAction
    data object ToggleEditStepsDialog: StepCounterAction
    data object OpenEditStepsDialog: StepCounterAction
    data object ToggleEditDateClick: StepCounterAction
    data object TogglePlayPause: StepCounterAction

    data class OnConfirmEditSteps(val date: LocalDate , val steps: Int): StepCounterAction

    data class OnConfirmEditDate(val date: LocalDate): StepCounterAction

    data object OnToggleResetStepsConfirmationDialog: StepCounterAction
    data object OnResetTodayStepsClick: StepCounterAction
}