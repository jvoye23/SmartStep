@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.features.step_counter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.design_system.components.SmartStepSheetDialog
import com.jvcodingsolutions.smartstep.design_system.components.StepCounterCard
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_PinLocation
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundMain
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import com.jvcodingsolutions.smartstep.features.step_counter.components.StepGoalBottomSheet
import com.jvcodingsolutions.smartstep.permissions.rememberStepPermissionState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.allow_access
import smartstep.composeapp.generated.resources.background_access_helps_info
import smartstep.composeapp.generated.resources.background_access_recommended
import smartstep.composeapp.generated.resources.`continue`
import smartstep.composeapp.generated.resources.enable_access_manually
import smartstep.composeapp.generated.resources.one_open_permissions
import smartstep.composeapp.generated.resources.open_settings
import smartstep.composeapp.generated.resources.three_select_allow
import smartstep.composeapp.generated.resources.to_count_your_steps_info
import smartstep.composeapp.generated.resources.to_track_your_steps_info
import smartstep.composeapp.generated.resources.two_tap_physical_activity

@Composable
fun StepCounterScreenRoot(
    viewModel: StepCounterViewModel = koinViewModel(),
    isStepGoalBottomSheetVisible: Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var permissionRequestCount by remember { mutableStateOf(0) }
    var showAllowAccessSheet by remember { mutableStateOf(false) }
    var showEnableAccessManuallySheet by remember { mutableStateOf(false) }
    var showBackgroundAccessSheet by remember { mutableStateOf(false) }
    var showStepGoalBottomSheet by remember { mutableStateOf(isStepGoalBottomSheetVisible) }


    val permissionState = rememberStepPermissionState(
        onPermissionResult = { isGranted, isPermanentlyDenied ->
            if (!isGranted) {
                if (isPermanentlyDenied) {
                    // System dialog is permanently suppressed, skip directly to manual enablement
                    showAllowAccessSheet = false
                    showEnableAccessManuallySheet = true
                } else {
                    // The user denied it once, but we can still show the system dialog if they try again
                    if (permissionRequestCount == 1) {
                        showAllowAccessSheet = true
                    } else if (permissionRequestCount >= 2) {
                        showAllowAccessSheet = false
                        showEnableAccessManuallySheet = true
                    }
                }
            } else {
                showAllowAccessSheet = false
                showEnableAccessManuallySheet = false
                showBackgroundAccessSheet = true
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionState.hasPermission && permissionRequestCount == 0) {
            permissionRequestCount++
            // Fire the request immediately on first launch
            // If they had previously denied it permanently, the OS will return immediately 
            // and the `isPermanentlyDenied` check inside `onPermissionResult` handles it.
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(showStepGoalBottomSheet) {
        viewModel.onAction(StepCounterAction.OnToggleStepGoalBottomSheet)
    }

    val displayState = state.copy(
        isAllowAccessBottomSheetVisible = showAllowAccessSheet,
        isEnableAccessManuallyBottomSheetVisible = showEnableAccessManuallySheet,
        isBackgroundAccessBottomSheetVisible = showBackgroundAccessSheet
    )

    StepCounterScreen(
        onAction = { action ->
            when(action) {
                StepCounterAction.OnNavigationMenuClick -> {}
                StepCounterAction.OnAllowAccessButtonClick -> {
                    permissionRequestCount++
                    permissionState.launchPermissionRequest()
                }
                StepCounterAction.OnOpenSettingsClick -> {
                    permissionState.openAppSettings()
                }
                StepCounterAction.OnContinueBackgroundAccessClick -> {
                    permissionState.requestBackgroundExecution()
                }
                else -> Unit
            }
            viewModel.onAction(action)
        },
        state = displayState,
        hasPermissions = permissionState.hasPermission,
    )
}

@Composable
private fun StepCounterScreen(
    onAction: (StepCounterAction) -> Unit,
    state: StepCounterState,
    hasPermissions: Boolean = false,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    MobilePortraitLayout(
        onAction = onAction,
        state = state,
        hasPermissions = hasPermissions
    )
}

@Composable
private fun MobilePortraitLayout(
    onAction: (StepCounterAction) -> Unit,
    state: StepCounterState,
    hasPermissions: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.backgroundMain)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepCounterCard(
            currentSteps = state.currentSteps,
            dailyGoalSteps = state.dailyGoalSteps,
        )
    }

    if(!hasPermissions) {
        if(state.isEnableAccessManuallyBottomSheetVisible) {
            EnableAccessManuallyBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onOpenSettingsClick = { onAction(StepCounterAction.OnOpenSettingsClick) }
            )
        } else if (state.isAllowAccessBottomSheetVisible) {
            AllowAccessBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onAllowAccessClick = { onAction(StepCounterAction.OnAllowAccessButtonClick) }
            )
        }
    } else {
        if (state.isBackgroundAccessBottomSheetVisible) {
            BackgroundAccessBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onContinueClick = { onAction(StepCounterAction.OnContinueBackgroundAccessClick) }
            )
        }
    }
    if(state.isStepGoalBottomSheetVisible) {
        StepGoalBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            onSave = { value ->
                onAction(StepCounterAction.OnSaveStepGoal(value))
            },
            onCancel = {
                onAction(StepCounterAction.OnToggleStepGoalBottomSheet)
            }
        )
    }
}

@Composable
private fun AllowAccessBottomSheet(
    modifier: Modifier = Modifier,
    onAllowAccessClick: () -> Unit
) {
    SmartStepSheetDialog(
        modifier = modifier,
        buttonText = stringResource(Res.string.allow_access),
        onDismissRequest = {},
        onConfirm = { onAllowAccessClick() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.backgroundSecondary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.backgroundWhite,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.strokeMain,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icon_PinLocation,
                    contentDescription = "Steps Icon",
                    tint = MaterialTheme.colorScheme.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.to_count_your_steps_info),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(44.dp))
        }
    }
}

@Composable
private fun EnableAccessManuallyBottomSheet(
    modifier: Modifier = Modifier,
    onOpenSettingsClick: () -> Unit
) {
    SmartStepSheetDialog(
        modifier = modifier,
        buttonText = stringResource(Res.string.open_settings),
        onDismissRequest = {},
        onConfirm = { onOpenSettingsClick() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.backgroundSecondary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.enable_access_manually),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.to_track_your_steps_info),
                style = MaterialTheme.typography.bodyLargeRegular,
                color = MaterialTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.one_open_permissions),
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.textPrimary,
                )
                Text(
                    text = stringResource(Res.string.two_tap_physical_activity),
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.textPrimary,
                )
                Text(
                    text = stringResource(Res.string.three_select_allow),
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.textPrimary,
                )

            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BackgroundAccessBottomSheet(
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit
) {
    SmartStepSheetDialog(
        modifier = modifier,
        buttonText = stringResource(Res.string.`continue`),
        onDismissRequest = {},
        onConfirm = { onContinueClick() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.backgroundSecondary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.background_access_recommended),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.background_access_helps_info),
                style = MaterialTheme.typography.bodyLargeRegular,
                color = MaterialTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}




@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun StepCounterScreenPreview() {
    SmartStepTheme {
        StepCounterScreen(
            onAction = {},
            state = StepCounterState(
                currentSteps = 4523,
                dailyGoalSteps = 6000
            ),
        )
    }
}