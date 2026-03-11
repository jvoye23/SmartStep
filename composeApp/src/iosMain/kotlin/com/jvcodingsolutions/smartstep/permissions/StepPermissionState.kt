package com.jvcodingsolutions.smartstep.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.CoreMotion.CMPedometer
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import kotlin.system.exitProcess

@Composable
actual fun rememberStepPermissionState(
    onPermissionResult: (isGranted: Boolean, isPermanentlyDenied: Boolean) -> Unit
): StepPermissionState {
    return remember(onPermissionResult) {
        object : StepPermissionState {
            override val hasPermission: Boolean = true
            override val isPermanentlyDenied: Boolean = false
            
            override fun launchPermissionRequest() {
                onPermissionResult(true, false)
            }

            override fun openAppSettings() {
                val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
                    UIApplication.sharedApplication.openURL(url)
                }
            }
            
            override fun requestBackgroundExecution() {
                openAppSettings()
            }

            override fun exitApp() {
                exitProcess(0)
            }
        }
    }
}
