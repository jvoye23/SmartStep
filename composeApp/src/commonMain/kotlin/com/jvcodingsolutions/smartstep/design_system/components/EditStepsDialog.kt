@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.core.presentation.util.formattedDate
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_ArrowDown
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumRegular
import com.jvcodingsolutions.smartstep.design_system.theme.bodySmallRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.cancel
import smartstep.composeapp.generated.resources.date
import smartstep.composeapp.generated.resources.edit_uppercase
import smartstep.composeapp.generated.resources.edit_steps_info
import smartstep.composeapp.generated.resources.save
import smartstep.composeapp.generated.resources.steps
import kotlin.time.Clock

@Composable
fun EditStepsDialog(
    initialDate: String,
    onDateClick: () -> Unit,
    steps: Int = 0,
    onDismiss: () -> Unit,
    onSave: (LocalDate, Int) -> Unit
) {
    var editedSteps by rememberSaveable(steps) { mutableStateOf(steps) }
    //var editedDate by rememberSaveable(initialDate) { mutableStateOf(initialDate) }

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
        onDismissRequest = onDismiss
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        val deviceWidthFraction: Float = when (deviceConfiguration) {
            DeviceConfiguration.TABLET_PORTRAIT,
            DeviceConfiguration.TABLET_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> 0.6f
            else -> 1f
        }

        Surface(
            modifier = Modifier.fillMaxWidth(fraction = deviceWidthFraction),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.backgroundSecondary
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = stringResource(Res.string.edit_uppercase),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.edit_steps_info),
                    style = MaterialTheme.typography.bodyMediumRegular,
                    color = MaterialTheme.colorScheme.textSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Date Section
                DropDownSection(
                    label = stringResource(Res.string.date),
                    value = initialDate,
                    hasArrow = true,
                    onClick = onDateClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Steps Section
                SmartStepTextField(
                    label = stringResource(Res.string.steps),
                    value = if (editedSteps == 0) "" else editedSteps.toString(),
                    onValueChange = { newValue ->
                        if (newValue.isEmpty()) {
                            editedSteps = 0
                        } else {
                            newValue.filter { it.isDigit() }.toIntOrNull()?.let {
                                editedSteps = it
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    TextButton(
                        onClick = {
                            val parsedDate = try {
                                LocalDate.parse(initialDate.replace("/", "-"))
                            } catch (e: Exception) {
                                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            }
                            onSave(parsedDate, editedSteps)
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.save),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropDownSection(
    label: String,
    value: String,
    hasArrow: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.strokeMain,
                shape = RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.backgroundWhite)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.textSecondary,
                style = MaterialTheme.typography.bodySmallRegular
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = MaterialTheme.colorScheme.textPrimary,
                style = MaterialTheme.typography.bodyLargeRegular
            )
        }

        if (hasArrow) {
            Icon(
                imageVector = Icon_ArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.textPrimary
            )
        }
    }
}

@Composable
private fun SmartStepTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.strokeMain,
                shape = RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.backgroundWhite),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.textSecondary,
                    style = MaterialTheme.typography.bodySmallRegular
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = "0",
                            color = MaterialTheme.colorScheme.textPrimary,
                            style = MaterialTheme.typography.bodyLargeRegular,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                    innerTextField()
                }
            }
        },
        textStyle = MaterialTheme.typography.bodyLargeRegular.copy(
            color = MaterialTheme.colorScheme.textPrimary
        )
    )
}

@Preview
@Composable
private fun EditStepsDialogPreview() {
    SmartStepTheme {
        EditStepsDialog(
            onDismiss = {},
            onSave = { _, _ -> },
            onDateClick = {},
            initialDate = formattedDate(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date),
            steps = 15
        )
    }
}
