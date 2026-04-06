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
import com.jvcodingsolutions.smartstep.core.presentation.util.formattedDate
import com.jvcodingsolutions.smartstep.design_system.components.DailyAverageCard
import com.jvcodingsolutions.smartstep.design_system.components.DatePickerDialog
import com.jvcodingsolutions.smartstep.design_system.components.EditStepsDialog
import com.jvcodingsolutions.smartstep.design_system.components.ResetStepsConfirmationDialog
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
import com.jvcodingsolutions.smartstep.navigation.SmartStepNavigationAction
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
    shouldOpenEditSteps: Boolean = false,
    onEditStepsOpened: () -> Unit = {},
    shouldOpenResetStepsDialog: Boolean = false,
    onResetStepsOpened: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var permissionRequestCount by remember { mutableStateOf(0) }
    var showAllowAccessSheet by remember { mutableStateOf(false) }
    var showEnableAccessManuallySheet by remember { mutableStateOf(false) }
    var showBackgroundAccessSheet by remember { mutableStateOf(false) }


    val permissionState = rememberStepPermissionState(
        onPermissionResult = { isGranted, isPermanentlyDenied ->
            if (!isGranted) {
                if (isPermanentlyDenied) {
                    showAllowAccessSheet = false
                    showEnableAccessManuallySheet = true
                } else {
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
                
                // If permissions are granted after initial launch, we must restart tracking
                viewModel.onAction(StepCounterAction.StartTracking)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionState.hasPermission && permissionRequestCount == 0) {
            permissionRequestCount++
            permissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(permissionState.hasPermission) {
        if (permissionState.hasPermission) {
            viewModel.onAction(StepCounterAction.StartTracking)
        }
    }

    LaunchedEffect(shouldOpenEditSteps) {
        if (shouldOpenEditSteps) {
            viewModel.onAction(StepCounterAction.OpenEditStepsDialog)
            onEditStepsOpened()
        }
    }

    LaunchedEffect( shouldOpenResetStepsDialog) {
        if(shouldOpenResetStepsDialog) {
            viewModel.onAction(StepCounterAction.OnToggleResetStepsConfirmationDialog)
            onResetStepsOpened()
        }
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
                    showBackgroundAccessSheet = false // Close immediately to avoid freeze
                    permissionState.requestBackgroundExecution()
                }
                StepCounterAction.ToggleAllowAccessBottomSheet -> {
                    showAllowAccessSheet = !showAllowAccessSheet
                }
                StepCounterAction.ToggleEnableAccessManuallyBottomSheet -> {
                    showEnableAccessManuallySheet = !showEnableAccessManuallySheet
                }
                StepCounterAction.ToggleBackgroundAccessBottomSheet -> {
                    showBackgroundAccessSheet = !showBackgroundAccessSheet
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
        hasPermissions = hasPermissions,
    )

    if(state.isEditStepsDialogVisible) {
        EditStepsDialog(
            initialDate = formattedDate(state.editedDate),
            onDateClick = { onAction(StepCounterAction.ToggleEditDateClick) },
            steps = state.currentSteps,
            onDismiss = { onAction(StepCounterAction.ToggleEditStepsDialog) },
            onSave = { date, steps ->
                onAction(StepCounterAction.OnConfirmEditSteps(date = date, steps = steps))
            }
        )
    }
    if(state.isDatePickerDialogVisible) {
        DatePickerDialog(
            initialDate = state.editedDate,
            onDismiss = { onAction(StepCounterAction.ToggleEditDateClick) },
            onConfirm = { date ->
                onAction(StepCounterAction.OnConfirmEditDate(date))
            }
        )
    }

    if(state.isResetStepsConfirmationDialogVisible) {
        ResetStepsConfirmationDialog(
            onCancel = {
                onAction(StepCounterAction.OnToggleResetStepsConfirmationDialog)
            },
            onReset = {
                onAction(StepCounterAction.OnResetTodayStepsClick)
            }
        )
    }
}

@Composable
private fun MobilePortraitLayout(
    onAction: (StepCounterAction) -> Unit,
    state: StepCounterState,
    hasPermissions: Boolean = false,
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
            onEditClick = { onAction(StepCounterAction.ToggleEditStepsDialog) },
            togglePlayPause = { onAction(StepCounterAction.TogglePlayPause) },
            distance = state.distanceTraveled,
            kcal = state.caloriesBurned,
            duration = state.activityDurationRaw,
            isPaused = state.isStepTrackerPaused,
        )
        Spacer(modifier = Modifier.height(8.dp))
        DailyAverageCard(state = state.dailyAverageState)
    }

    if(!hasPermissions) {
        if(state.isEnableAccessManuallyBottomSheetVisible) {
            EnableAccessManuallyBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { onAction(StepCounterAction.ToggleEnableAccessManuallyBottomSheet) },
                onOpenSettingsClick = { onAction(StepCounterAction.OnOpenSettingsClick) }
            )
        } else if (state.isAllowAccessBottomSheetVisible) {
            AllowAccessBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { onAction(StepCounterAction.ToggleAllowAccessBottomSheet) },
                onAllowAccessClick = { onAction(StepCounterAction.OnAllowAccessButtonClick) }
            )
        }
    } else {
        if (state.isBackgroundAccessBottomSheetVisible) {
            BackgroundAccessBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = { onAction(StepCounterAction.ToggleBackgroundAccessBottomSheet) },
                onContinueClick = { onAction(StepCounterAction.OnContinueBackgroundAccessClick) }
            )
        }
    }
}

@Composable
private fun AllowAccessBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onAllowAccessClick: () -> Unit
) {
    SmartStepSheetDialog(
        modifier = modifier,
        buttonText = stringResource(Res.string.allow_access),
        onDismissRequest = onDismissRequest,
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
    onDismissRequest: () -> Unit,
    onOpenSettingsClick: () -> Unit
) {
    SmartStepSheetDialog(
        modifier = modifier,
        buttonText = stringResource(Res.string.open_settings),
        onDismissRequest = onDismissRequest,
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
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
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
    onDismissRequest: () -> Unit,
    onContinueClick: () -> Unit
) {
    SmartStepSheetDialog(
        modifier = modifier,
        buttonText = stringResource(Res.string.`continue`),
        onDismissRequest = onDismissRequest,
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
            Spacer(modifier = Modifier.height(44.dp))
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
                currentSteps = 2410,
                dailyGoalSteps = 6000
            ),
            hasPermissions = true
        )
    }
}
