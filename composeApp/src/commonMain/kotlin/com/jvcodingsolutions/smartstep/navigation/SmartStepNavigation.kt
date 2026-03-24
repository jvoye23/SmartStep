@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.design_system.components.SmartStepCloseAppDialog
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_MenuBurgerSquare
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundMain
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.features.profile_setup.ProfileSetupScreenRoot
import com.jvcodingsolutions.smartstep.features.step_counter.StepCounterAction
import com.jvcodingsolutions.smartstep.features.step_counter.StepCounterScreenRoot
import com.jvcodingsolutions.smartstep.features.step_counter.components.StepGoalBottomSheet
import com.jvcodingsolutions.smartstep.permissions.rememberStepPermissionState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.my_profile
import smartstep.composeapp.generated.resources.smart_step

@Composable
fun SmartStepNavigation(
    modifier: Modifier = Modifier,
    viewModel: SmartStepNavigationViewModel = koinViewModel(),
    onNavigateToProfileSettings: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // A CoroutineScope is needed to run the drawer open/close animations
    val scope = rememberCoroutineScope()

    val smartStepNavigationState = rememberNavigationState(
        startRoute = Route.StepCounterRoute.StepGoalRoute,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
    )
    val navigator = remember {
        Navigator(smartStepNavigationState)
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val topAppBarBackgroundColor = when(deviceConfiguration){
        DeviceConfiguration.MOBILE_PORTRAIT -> MaterialTheme.colorScheme.backgroundWhite
        else -> Color.Transparent
    }

    // Pass an empty callback for now. The StepCounterScreenRoot handles its own permission requests,
    // but we can trigger the OS intent here via the drawer.
    val permissionState = rememberStepPermissionState()

    SmartStepNavigationDrawer(
        selectedKey = Route.ProfileOnboardingRoute.ProfileSetupScreenRoute,
        hasBackgroundPermission = permissionState.hasBackgroundPermission,
        onFixStopCountingIssueClick = {
            scope.launch {
                drawerState.close()
                permissionState.requestBackgroundExecution()
            }
        },
        onSelectKey = { destinationRoute ->
            scope.launch {
                drawerState.close()
                //navigator.navigate(destinationRoute)
                onNavigateToProfileSettings()
            }

        },
        drawerState = drawerState,
        onShowExitDialog = {
            scope.launch {
                drawerState.close()
                viewModel.onAction(SmartStepNavigationAction.ShowExitDialog)
            }
        },
        onStepGoalClick = {
            scope.launch {
                drawerState.close()
                viewModel.onAction(SmartStepNavigationAction.OnToggleStepGoalBottomSheet)
            }
        },
        onPersonalSettingsClick = {
            scope.launch {
                drawerState.close()
                onNavigateToProfileSettings()
            }
        }



    ){
        Scaffold(
            containerColor = when(smartStepNavigationState.topLevelRoute){
                Route.StepCounterRoute.StepGoalRoute -> {
                    MaterialTheme.colorScheme.backgroundMain
                }
                else -> {
                    MaterialTheme.colorScheme.backgroundMain
                }
            },
            modifier = modifier.statusBarsPadding(),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    } else {
                                        drawerState.open()
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icon_MenuBurgerSquare,
                                contentDescription = "Menu Icon",
                                tint = MaterialTheme.colorScheme.textPrimary
                            )
                        }
                    },
                    modifier = Modifier,
                    title = {
                        when(smartStepNavigationState.topLevelRoute){
                            Route.StepCounterRoute.StepGoalRoute -> {
                                Text(
                                    text = stringResource(Res.string.smart_step),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.textPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = topAppBarBackgroundColor
                    ),
                    windowInsets = WindowInsets.statusBars
                )
            }

        ) { innerPadding ->
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onBack = navigator::goBack,
                entries = smartStepNavigationState.toEntries(
                    entryProvider {
                        entry<Route.StepCounterRoute.StepGoalRoute> {
                            StepCounterScreenRoot()
                        }
                    }
                )
            )
        }

    }
    if(state.isExitDialogVisible) {
        SmartStepCloseAppDialog(
            onConfirm = {
                permissionState.exitApp()
            }
        )
    }

    if(state.isStepGoalDialogVisible) {
        StepGoalBottomSheet(
            modifier = Modifier.fillMaxWidth(),
            onSave = { value ->
                viewModel.onAction(SmartStepNavigationAction.OnSaveStepGoal(value))
                println("StepGoal: $value")
            },
            onCancel = {
                viewModel.onAction(SmartStepNavigationAction.OnToggleStepGoalBottomSheet)
            }
        )
    }



}

@Preview
@Composable
private fun SmartStepNavigationPreview() {
    SmartStepTheme {
        SmartStepNavigation(
            onNavigateToProfileSettings = {}
        )
    }
}