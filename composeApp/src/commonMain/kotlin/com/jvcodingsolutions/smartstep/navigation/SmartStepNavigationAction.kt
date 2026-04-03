package com.jvcodingsolutions.smartstep.navigation

sealed interface SmartStepNavigationAction {
    data object OnToggleStepGoalBottomSheet: SmartStepNavigationAction

    data object ShowExitDialog: SmartStepNavigationAction
    data object OnFixStopCountingIssueClick: SmartStepNavigationAction
    data class OnSaveStepGoal(val value: Int): SmartStepNavigationAction
}