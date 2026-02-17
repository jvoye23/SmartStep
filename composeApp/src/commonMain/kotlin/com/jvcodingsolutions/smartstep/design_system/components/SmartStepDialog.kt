package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.the_app_will_close_alert

@Composable
fun SmartStepDialog(
    icon: @Composable () -> Unit,
    dialogText: String,
    buttonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.backgroundSecondary
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = dialogText,
                    style = MaterialTheme.typography.bodyLargeRegular,
                    color = MaterialTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                SmartStepFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                    buttonText = buttonText,
                    enabled = true
                )

            }
        }

    }


}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SmartStepDialogPreview() {
    SmartStepTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SmartStepDialog(
                icon = {
                    Icon(
                        imageVector = Icon_PowerTurnOn,
                        contentDescription = "Icon Power Turn On",
                        tint = MaterialTheme.colorScheme.textPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                },
                buttonText = "Ok",
                onDismiss = {},
                onConfirm = {},
                dialogText = stringResource(Res.string.the_app_will_close_alert)
            )
        }
    }
}
