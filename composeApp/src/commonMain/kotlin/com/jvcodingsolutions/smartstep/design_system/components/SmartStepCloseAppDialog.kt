package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_PowerTurnOn
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.ok
import smartstep.composeapp.generated.resources.the_app_will_close_alert

@Composable
fun SmartStepCloseAppDialog(
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
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
                Icon(
                    imageVector = Icon_PowerTurnOn,
                    tint = MaterialTheme.colorScheme.textPrimary,
                    contentDescription = "Power Icon"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.the_app_will_close_alert),
                    style = MaterialTheme.typography.bodyLargeRegular,
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                SmartStepFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                    buttonText = stringResource(Res.string.ok)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SmartStepCloseAppDialogPreview() {
    SmartStepCloseAppDialog(
        onConfirm = {}
    )
}