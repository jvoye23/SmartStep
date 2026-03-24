package com.jvcodingsolutions.smartstep.navigation

import com.jvcodingsolutions.smartstep.features.step_counter.StepCounterAction

sealed interface SmartStepNavigationAction {

    data object OnToggleStepGoalBottomSheet: SmartStepNavigationAction
    data object ShowExitDialog: SmartStepNavigationAction
    data class OnSaveStepGoal(val value: Int): SmartStepNavigationAction
}