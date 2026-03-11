package com.jvcodingsolutions.smartstep.features.step_counter

data class StepCounterState(
    val currentSteps: Int = 0,
    val dailyGoalSteps: Int = 6000,
    val isPermissionRequested: Boolean = false,
    val isAllowAccessBottomSheetVisible: Boolean = false,
    val isEnableAccessManuallyBottomSheetVisible: Boolean = false,
    val isBackgroundAccessBottomSheetVisible: Boolean = true,
    val isStepGoalBottomSheetVisible: Boolean = false,
    val currentSelectedStepGoalValue: Int = 2000
)
