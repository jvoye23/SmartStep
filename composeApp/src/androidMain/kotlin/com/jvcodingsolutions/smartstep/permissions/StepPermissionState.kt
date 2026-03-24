package com.jvcodingsolutions.smartstep.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlin.system.exitProcess

@Composable
actual fun rememberStepPermissionState(
    onPermissionResult: (isGranted: Boolean, isPermanentlyDenied: Boolean) -> Unit
): StepPermissionState {
    val context = LocalContext.current
    val permission = Manifest.permission.ACTIVITY_RECOGNITION
    val activity = context as? Activity

    var hasPermission by remember {
        mutableStateOf(
            try {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        )
    }

    var isPermanentlyDenied by remember {
        mutableStateOf(false)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                try {
                    val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    if (isGranted != hasPermission) {
                        hasPermission = isGranted
                        if (isGranted) {
                            isPermanentlyDenied = false
                            onPermissionResult(true, false)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        val rationale = try {
            activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } ?: false
        } catch (e: Exception) {
            // If the service binding fails, assume we should show rationale or handle gracefully
            false
        }
        val permanentlyDenied = !isGranted && !rationale
        isPermanentlyDenied = permanentlyDenied
        onPermissionResult(isGranted, permanentlyDenied)
    }

    return remember(hasPermission, isPermanentlyDenied, launcher, permission, context) {
        object : StepPermissionState {
            override val hasPermission: Boolean = hasPermission
            override val isPermanentlyDenied: Boolean = isPermanentlyDenied
            
            override fun launchPermissionRequest() {
                try {
                    if (!hasPermission) {
                        launcher.launch(permission)
                    } else {
                        onPermissionResult(true, false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun openAppSettings() {
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun requestBackgroundExecution() {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun exitApp() {
                activity?.finishAndRemoveTask()
                exitProcess(0)
            }
        }
    }
}
