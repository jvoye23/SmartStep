package com.jvcodingsolutions.smartstep.features.report.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.buttonSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.next_week
import smartstep.composeapp.generated.resources.previous_week

@Composable
fun WeekSelector(
    modifier: Modifier = Modifier,
    weekRangeLabel: String,
    isNextWeekEnabled: Boolean,
    onPreviousWeekClick: () -> Unit,
    onNextWeekClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousWeekClick,
            shape = CircleShape,
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.buttonPrimary,
                contentColor = MaterialTheme.colorScheme.textWhite
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(Res.string.previous_week),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = weekRangeLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.textPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(
            onClick = onNextWeekClick,
            enabled = isNextWeekEnabled,
            shape = CircleShape,
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.buttonPrimary,
                contentColor = MaterialTheme.colorScheme.textWhite,
                disabledContainerColor = MaterialTheme.colorScheme.buttonSecondary,
                disabledContentColor = MaterialTheme.colorScheme.textWhite
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.next_week),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun WeekSelectorPreview() {
    SmartStepTheme {
        WeekSelector(
            weekRangeLabel = "Nov 16 – Nov 22",
            isNextWeekEnabled = false,
            onPreviousWeekClick = {},
            onNextWeekClick = {}
        )
    }
}
