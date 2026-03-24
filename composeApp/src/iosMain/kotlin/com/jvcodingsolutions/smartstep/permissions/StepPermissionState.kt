package com.jvcodingsolutions.smartstep.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberStepPermissionState(
    onPermissionResult: (isGranted: Boolean, isPermanentlyDenied: Boolean) -> Unit
): StepPermissionState {
    // Stub implementation for iOS for now. You'll use Apple's HealthKit or CMMotionActivityManager permissions here
    return remember {
        object : StepPermissionState {
            override val hasPermission: Boolean = true
            override val hasBackgroundPermission: Boolean = true
            override val isPermanentlyDenied: Boolean = false
            override fun launchPermissionRequest() {
                onPermissionResult(true, false)
            }
            override fun openAppSettings() {}
            override fun requestBackgroundExecution() {}
            override fun exitApp() {}
        }
    }
}
