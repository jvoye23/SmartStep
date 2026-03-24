package com.jvcodingsolutions.smartstep.features.step_counter

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
}