@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.features.profile_setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.core.presentation.util.ObserveAsEvents
import com.jvcodingsolutions.smartstep.design_system.components.HeightPickerDialog
import com.jvcodingsolutions.smartstep.design_system.components.SmartStepDropDown
import com.jvcodingsolutions.smartstep.design_system.components.SmartStepFilledButton
import com.jvcodingsolutions.smartstep.design_system.components.WeightPickerDialog
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.gender
import smartstep.composeapp.generated.resources.height
import smartstep.composeapp.generated.resources.my_profile
import smartstep.composeapp.generated.resources.personal_settings
import smartstep.composeapp.generated.resources.save
import smartstep.composeapp.generated.resources.skip
import smartstep.composeapp.generated.resources.start
import smartstep.composeapp.generated.resources.this_information_helps_calculate
import smartstep.composeapp.generated.resources.weight

@Composable
fun ProfileSetupScreenRoot(
    onSkipClick: () -> Unit = {},
    onNavigateBackClick: () -> Unit,
    isOnboarding: Boolean,
    viewModel: ProfileSetupViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is ProfileSetupEvent.Error -> {
                /* Toast.makeText(
                     context,
                     event.error.asString(context),
                     Toast.LENGTH_LONG
                 ).show()*/
            }
            ProfileSetupEvent.ProfileIsSaved -> {
                /*Toast.makeText(
                    context,
                    R.string.tasky_item_saved,
                    Toast.LENGTH_LONG
                ).show()*/
                onNavigateBackClick()
            }
        }
    }
    ProfileSetupScreen(
        state = state,
        onAction = { action ->
            when(action) {
                ProfileSetupAction.OnSkipClick -> onSkipClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        isOnboarding = isOnboarding
    )
}

@Composable
fun ProfileSetupScreen(
    state: ProfileSetupState,
    onAction: (ProfileSetupAction) -> Unit,
    isOnboarding: Boolean
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val topAppBarBackgroundColor = when(deviceConfiguration){
        DeviceConfiguration.MOBILE_PORTRAIT -> MaterialTheme.colorScheme.backgroundWhite
        else -> Color.Transparent
    }
    Surface(
    ) {
        Scaffold(
            topBar = {
                if(isOnboarding){
                    CenterAlignedTopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        title = {
                            Text(
                                text = stringResource(Res.string.my_profile),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.textPrimary
                            )
                        },
                        actions = {
                            if(isOnboarding) {
                                TextButton(
                                    onClick = { onAction(ProfileSetupAction.OnSkipClick) },
                                ) {
                                    Text(
                                        text = stringResource(Res.string.skip),
                                        style = MaterialTheme.typography.bodyLargeMedium,
                                        color = MaterialTheme.colorScheme.buttonPrimary
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
            }
        ) { innerPadding ->

            when(deviceConfiguration) {
                DeviceConfiguration.MOBILE_PORTRAIT -> {
                    MobilePortraitLayout(
                        modifier = Modifier.padding(innerPadding),
                        state = state,
                        onAction = onAction,
                        isOnboarding = isOnboarding
                    )
                }
                DeviceConfiguration.MOBILE_LANDSCAPE -> Unit
                DeviceConfiguration.TABLET_PORTRAIT,
                DeviceConfiguration.TABLET_LANDSCAPE,
                DeviceConfiguration.DESKTOP -> {
                    LargeScreenLayout(
                        modifier = Modifier.padding(innerPadding),
                        state = state,
                        onAction = onAction,
                        isOnboarding = isOnboarding
                    )
                }
            }

        }
        if(state.isHeightPickerVisible) {
            HeightPickerDialog(
                initialHeight = state.selectedHeight,
                onDismiss = { onAction(ProfileSetupAction.OnToggleHeightDropdownClick) },
                onConfirm = { onAction(ProfileSetupAction.OnConfirmSelectedHeight(it))}
            )
        }
        if(state.isWeightPickerVisible) {
            WeightPickerDialog(
                initialWeight = state.selectedWeight,
                onDismiss = { onAction(ProfileSetupAction.OnToggleWeightDropdownClick) },
                onConfirm = { onAction(ProfileSetupAction.OnConfirmSelectedWeight(it))}
            )
        }

    }
}

@Composable
private fun MobilePortraitLayout(
    modifier: Modifier = Modifier,
    onAction: (ProfileSetupAction) -> Unit,
    state: ProfileSetupState = ProfileSetupState(),
    isOnboarding: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.backgroundSecondary)
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
            .padding(bottom = 16.dp)
    ) {
        DropDownSection(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            state = state,
            onAction = onAction,
            isOnboarding = isOnboarding
        )

        SmartStepFilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = {onAction(ProfileSetupAction.OnStartClick)},
            buttonText = if(isOnboarding) {
                stringResource(Res.string.start)
            } else {
                stringResource(Res.string.save)
            },
            enabled = true,
        )

    }
}

@Composable
private fun LargeScreenLayout(
    modifier: Modifier = Modifier,
    onAction: (ProfileSetupAction) -> Unit,
    state: ProfileSetupState = ProfileSetupState(),
    isOnboarding: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.backgroundSecondary)
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
            .padding(bottom = 16.dp)
    ) {
        DropDownSection(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .align(Alignment.TopCenter),
            state = state,
            onAction = onAction,
            isOnboarding = isOnboarding
        )

        SmartStepFilledButton(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .align(Alignment.BottomCenter),
            onClick = {onAction(ProfileSetupAction.OnStartClick)},
            buttonText = stringResource(Res.string.start),
            enabled = true,
        )
    }
}


@Composable
private fun DropDownSection(
    modifier: Modifier = Modifier,
    onAction: (ProfileSetupAction) -> Unit,
    state: ProfileSetupState,
    isOnboarding: Boolean
) {

    val onBoardingModifier: Modifier = Modifier
        .fillMaxWidth()
        .background(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.backgroundWhite
        )
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.strokeMain,
            shape = RoundedCornerShape(14.dp)
        )
        .padding(16.dp)

    val profileSettingsModifier: Modifier = Modifier
        .fillMaxWidth()

    Column(
        modifier = modifier
    ) {
        Text(
            text = if(isOnboarding) {
                stringResource(Res.string.this_information_helps_calculate)
            } else {
                stringResource(Res.string.personal_settings)
            },
            modifier = Modifier
                .padding(horizontal = 0.dp),
            style = if(isOnboarding) {
                MaterialTheme.typography.bodyLargeRegular
            } else {
                MaterialTheme.typography.bodyLargeMedium
            },
            color = MaterialTheme.colorScheme.textPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = if(isOnboarding) onBoardingModifier else profileSettingsModifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmartStepDropDown(
                label = stringResource(Res.string.gender),
                options = state.genderOptions,
                expanded = state.isGenderDropdownExpanded,
                onExpandedChange = {
                    onAction(ProfileSetupAction.OnGenderDropdownClick)
                },
                selectedOption = state.selectedGender.label,
                onOptionSelected = { onAction(ProfileSetupAction.OnGenderOptionSelected(option = it)) },
                containerColor = if(isOnboarding) MaterialTheme.colorScheme.backgroundSecondary else
                    MaterialTheme.colorScheme.backgroundWhite
            )

            SmartStepDropDown(
                label = stringResource(Res.string.height),
                onExpandedChange = {
                    onAction(ProfileSetupAction.OnToggleHeightDropdownClick)
                },
                selectedOption = state.selectedHeight.toCm().toString() + " cm",
                onOptionSelected = { onAction(ProfileSetupAction.OnToggleHeightDropdownClick) },
                containerColor = if(isOnboarding) MaterialTheme.colorScheme.backgroundSecondary else
                    MaterialTheme.colorScheme.backgroundWhite
            )
            SmartStepDropDown(
                label = stringResource(Res.string.weight),
                onExpandedChange = {
                    onAction(ProfileSetupAction.OnToggleWeightDropdownClick)
                },
                selectedOption = if (state.selectedWeight.lbs == null) {
                    state.selectedWeight.kg.toString() + " kg"
                } else {
                    state.selectedWeight.lbs.toString()
                },
                onOptionSelected = { onAction(ProfileSetupAction.OnToggleHeightDropdownClick) },
                containerColor = if(isOnboarding) MaterialTheme.colorScheme.backgroundSecondary else
                    MaterialTheme.colorScheme.backgroundWhite
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileSetupScreenPreview() {
    ProfileSetupScreen(
        state = ProfileSetupState(),
        onAction = { },
        isOnboarding = false
    )
}

