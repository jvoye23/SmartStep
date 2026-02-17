package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary

@Composable
fun SmartStepOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String,
    enabled: Boolean = true
) {
    Button(
        onClick = {onClick()},
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.backgroundWhite,
            contentColor = MaterialTheme.colorScheme.textPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.backgroundWhite,
            disabledContentColor = MaterialTheme.colorScheme.textPrimary.copy(alpha = 0.4f),
        ),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.strokeMain
        ),
        contentPadding = PaddingValues(all = 10.dp),
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.bodyLargeMedium
        )
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SmartStepOutlinedButtonPreview() {
    SmartStepTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            SmartStepOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
                buttonText = "Button",
                enabled = false
            )

        }
    }
}
