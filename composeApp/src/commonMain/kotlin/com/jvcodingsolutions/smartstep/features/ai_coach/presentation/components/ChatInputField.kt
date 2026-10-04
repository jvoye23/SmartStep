package com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_SendMessage
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.ask_me_anything
import smartstep.composeapp.generated.resources.online_connection_required
import smartstep.composeapp.generated.resources.send_message

@Composable
fun ChatInputField(
    modifier: Modifier = Modifier,
    inputText: String,
    isOnline: Boolean,
    canSend: Boolean,
    onInputChanged: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChanged,
            modifier = Modifier.weight(1f),
            // Offline: the field stays visible but cannot gain focus or open the keyboard
            enabled = isOnline,
            placeholder = {
                Text(
                    text = if (isOnline) {
                        stringResource(Res.string.ask_me_anything)
                    } else {
                        stringResource(Res.string.online_connection_required)
                    },
                    style = MaterialTheme.typography.bodyLargeRegular,
                    color = MaterialTheme.colorScheme.textSecondary
                )
            },
            trailingIcon = if (!isOnline) {
                {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = stringResource(Res.string.online_connection_required),
                        tint = MaterialTheme.colorScheme.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            textStyle = MaterialTheme.typography.bodyLargeRegular,
            // Grows from a single line up to 5 lines, then the content scrolls
            minLines = 1,
            maxLines = 5,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.buttonPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.strokeMain,
                disabledBorderColor = MaterialTheme.colorScheme.strokeMain,
                focusedContainerColor = MaterialTheme.colorScheme.backgroundWhite,
                unfocusedContainerColor = MaterialTheme.colorScheme.backgroundWhite,
                disabledContainerColor = MaterialTheme.colorScheme.backgroundWhite,
                focusedTextColor = MaterialTheme.colorScheme.textPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.textPrimary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSendClick,
            enabled = canSend,
            shape = CircleShape,
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.buttonPrimary,
                contentColor = MaterialTheme.colorScheme.textWhite,
                disabledContainerColor = MaterialTheme.colorScheme.buttonPrimary.copy(alpha = 0.4f),
                disabledContentColor = MaterialTheme.colorScheme.textWhite.copy(alpha = 0.6f)
            )
        ) {
            Icon(
                imageVector = Icon_SendMessage,
                contentDescription = stringResource(Res.string.send_message),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ChatInputFieldPreview() {
    SmartStepTheme {
        ChatInputField(
            modifier = Modifier.padding(16.dp),
            inputText = "What do you reco",
            isOnline = true,
            canSend = true,
            onInputChanged = {},
            onSendClick = {}
        )
    }
}

@Preview
@Composable
private fun ChatInputFieldOfflinePreview() {
    SmartStepTheme {
        ChatInputField(
            modifier = Modifier.padding(16.dp),
            inputText = "",
            isOnline = false,
            canSend = false,
            onInputChanged = {},
            onSendClick = {}
        )
    }
}
