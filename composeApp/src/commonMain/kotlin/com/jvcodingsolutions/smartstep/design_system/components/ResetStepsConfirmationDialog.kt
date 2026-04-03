package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.are_you_sure_you_want_to_reset
import smartstep.composeapp.generated.resources.cancel
import smartstep.composeapp.generated.resources.reset

@Composable
fun ResetStepsConfirmationDialog(
    onCancel: () -> Unit,
    onReset: () -> Unit
) {
    Dialog(
        onDismissRequest = { onCancel() },
    ) {
        Surface(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.backgroundWhite
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.are_you_sure_you_want_to_reset),
                    style = MaterialTheme.typography.bodyLargeRegular,
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    TextButton(
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onReset()
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.reset),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ResetStepsConfirmationDialog() {
    ResetStepsConfirmationDialog(
        onCancel = {},
        onReset = {}
    )
}