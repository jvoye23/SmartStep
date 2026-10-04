package com.jvcodingsolutions.smartstep.features.report.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite
import com.jvcodingsolutions.smartstep.design_system.theme.titleAccent
import com.jvcodingsolutions.smartstep.features.report.ReportMetric
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.daily_average_format
import smartstep.composeapp.generated.resources.this_week

@Composable
fun ReportSummaryCard(
    modifier: Modifier = Modifier,
    metric: ReportMetric,
    isMetricSystem: Boolean,
    isCurrentWeek: Boolean,
    weeklyTotal: String,
    dailyAverage: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.buttonPrimary),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = metric.summaryTitle(isMetricSystem),
                    style = MaterialTheme.typography.bodyLargeMedium,
                    color = MaterialTheme.colorScheme.textWhite
                )
                if (isCurrentWeek) {
                    Text(
                        text = stringResource(Res.string.this_week),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.textWhite
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = weeklyTotal,
                style = MaterialTheme.typography.titleAccent,
                color = MaterialTheme.colorScheme.textWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    Res.string.daily_average_format,
                    dailyAverage,
                    metric.averageUnit(isMetricSystem)
                ),
                style = MaterialTheme.typography.bodyMediumRegular,
                color = MaterialTheme.colorScheme.textWhite
            )
        }
    }
}

@Preview
@Composable
private fun ReportSummaryCardPreview() {
    SmartStepTheme {
        ReportSummaryCard(
            metric = ReportMetric.STEPS,
            isMetricSystem = true,
            isCurrentWeek = true,
            weeklyTotal = "573",
            dailyAverage = "191"
        )
    }
}

@Preview
@Composable
private fun ReportSummaryCardDistancePreview() {
    SmartStepTheme {
        ReportSummaryCard(
            metric = ReportMetric.DISTANCE,
            isMetricSystem = true,
            isCurrentWeek = true,
            weeklyTotal = "3.2",
            dailyAverage = "2.5"
        )
    }
}
