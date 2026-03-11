package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_PinLocation
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundMain
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.to_count_your_steps_info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartStepSheetDialog(
    modifier: Modifier = Modifier,
    buttonText: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    bottomSheetContent: @Composable () -> Unit,
) {
    var allowClose by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { sheetValue ->
            // Returning 'false' prevents the sheet from snapping to Hidden when clicking the scrim
            // outside the BottomSheet
            if(sheetValue != SheetValue.Hidden) {
                true
            } else  {
                allowClose
            }
        }
    )

    val scope = rememberCoroutineScope()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> {
            ModalBottomSheet(
                onDismissRequest = {
                    // This will only be called if confirmValueChange returns true
                    onDismissRequest()
                },
                modifier = modifier.fillMaxWidth(),
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.backgroundSecondary,
            ) {
                SheetContent(
                    bottomSheetContent = { bottomSheetContent()},
                    onConfirm = {
                        // User clicked the button: Allow the close animation to happen
                        allowClose = true
                        onConfirm()
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismissRequest()
                            }
                        }
                    },
                    buttonText = buttonText
                )
            }
        }
        DeviceConfiguration.MOBILE_LANDSCAPE,
        DeviceConfiguration.TABLET_PORTRAIT,
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> {
            Dialog(
                onDismissRequest = { onDismissRequest() },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.backgroundSecondary,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.3f)

                ) {
                    SheetContent(
                        modifier = Modifier
                            .padding(24.dp),
                        bottomSheetContent = { bottomSheetContent()},

                        onConfirm = {
                            // User clicked the button: Allow the close animation to happen
                            allowClose = true
                            onConfirm()
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDismissRequest()
                                }
                            }
                        },
                        buttonText = buttonText
                    )
                }
            }
        }
    }


}

@Composable
private fun SheetContent(
    modifier: Modifier = Modifier,
    bottomSheetContent: @Composable () -> Unit,
    onConfirm: () -> Unit,
    buttonText: String,
) {
    Surface(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.backgroundSecondary)
                .padding(horizontal = 16.dp)
        ) {
            bottomSheetContent()

            SmartStepFilledButton(
                onClick = {onConfirm()},
                modifier = Modifier.fillMaxWidth(),
                buttonText = buttonText,
                enabled = true
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SmartStepSheetDialogPreview() {
    SmartStepTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SmartStepSheetDialog(
                modifier = Modifier,
                buttonText = "Allow access",
                onDismissRequest = {},
                onConfirm = {},
                bottomSheetContent = {
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
            )
        }
    }
}