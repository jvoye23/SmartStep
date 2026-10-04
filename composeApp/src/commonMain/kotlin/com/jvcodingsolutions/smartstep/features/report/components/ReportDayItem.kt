package com.jvcodingsolutions.smartstep.features.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Clock
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumRegular
import com.jvcodingsolutions.smartstep.design_system.theme.bodySmallRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.buttonSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import com.jvcodingsolutions.smartstep.features.report.DayStatus
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.day_status_completed
import smartstep.composeapp.generated.resources.day_status_in_progress
import smartstep.composeapp.generated.resources.goal_steps_format
import smartstep.composeapp.generated.resources.no_data

@Composable
fun ReportDayItem(
    modifier: Modifier = Modifier,
    dayName: String,
    value: String,
    unit: String,
    status: DayStatus,
    goalSteps: Int?,
    isToday: Boolean
) {
    val isMuted = status == DayStatus.NO_DATA
    val shape = RoundedCornerShape(16.dp)

    val dayNameColor = when {
        isToday -> MaterialTheme.colorScheme.buttonPrimary
        isMuted -> MaterialTheme.colorScheme.textSecondary
        else -> MaterialTheme.colorScheme.textPrimary
    }
    val valueColor = when {
        isToday -> MaterialTheme.colorScheme.buttonPrimary
        isMuted -> MaterialTheme.colorScheme.textSecondary
        else -> MaterialTheme.colorScheme.textPrimary
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.backgroundWhite,
                shape = shape
            )
            .border(
                width = if (isToday) 1.5.dp else 1.dp,
                color = if (isToday) {
                    MaterialTheme.colorScheme.buttonPrimary
                } else {
                    MaterialTheme.colorScheme.strokeMain
                },
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.bodyLargeMedium,
                color = dayNameColor
            )
            DayStatusIcon(status = status)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = valueColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmallRegular,
                    color = MaterialTheme.colorScheme.textSecondary
                )
            }
            when {
                status == DayStatus.NO_DATA -> {
                    Text(
                        text = stringResource(Res.string.no_data),
                        style = MaterialTheme.typography.bodySmallRegular,
                        color = MaterialTheme.colorScheme.textSecondary
                    )
                }
                goalSteps != null -> {
                    Text(
                        text = stringResource(Res.string.goal_steps_format, goalSteps),
                        style = MaterialTheme.typography.bodySmallRegular,
                        color = MaterialTheme.colorScheme.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DayStatusIcon(status: DayStatus) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                color = MaterialTheme.colorScheme.buttonSecondary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when (status) {
            DayStatus.DONE -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.day_status_completed),
                tint = MaterialTheme.colorScheme.buttonPrimary,
                modifier = Modifier.size(14.dp)
            )
            DayStatus.IN_PROGRESS -> Icon(
                imageVector = Icon_Clock,
                contentDescription = stringResource(Res.string.day_status_in_progress),
                tint = MaterialTheme.colorScheme.buttonPrimary,
                modifier = Modifier.size(14.dp)
            )
            DayStatus.NO_DATA -> Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(Res.string.no_data),
                tint = MaterialTheme.colorScheme.textSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ReportDayItemPreview() {
    SmartStepTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReportDayItem(
                dayName = "Monday",
                value = "245",
                unit = "steps",
                status = DayStatus.DONE,
                goalSteps = 6000,
                isToday = false
            )
            ReportDayItem(
                dayName = "Thursday",
                value = "0",
                unit = "steps",
                status = DayStatus.IN_PROGRESS,
                goalSteps = 6000,
                isToday = true
            )
            ReportDayItem(
                dayName = "Friday",
                value = "0",
                unit = "steps",
                status = DayStatus.NO_DATA,
                goalSteps = null,
                isToday = false
            )
        }
    }
}
