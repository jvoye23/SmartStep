package com.jvcodingsolutions.smartstep.permissions

import androidx.compose.runtime.Composable

interface StepPermissionState {
    val hasPermission: Boolean
    val hasBackgroundPermission: Boolean
    val isPermanentlyDenied: Boolean
    fun launchPermissionRequest()
    fun openAppSettings()
    fun requestBackgroundExecution()
    fun exitApp()
}

@Composable
expect fun rememberStepPermissionState(
    onPermissionResult: (isGranted: Boolean, isPermanentlyDenied: Boolean) -> Unit = { _, _ -> }
): StepPermissionState
