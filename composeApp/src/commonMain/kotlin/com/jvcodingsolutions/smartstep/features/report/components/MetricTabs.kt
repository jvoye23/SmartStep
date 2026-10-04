package com.jvcodingsolutions.smartstep.features.report.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodySmallRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.buttonSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import com.jvcodingsolutions.smartstep.features.report.ReportMetric

@Composable
fun MetricTabs(
    modifier: Modifier = Modifier,
    selectedMetric: ReportMetric,
    onMetricSelected: (ReportMetric) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.backgroundWhite)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.strokeMain
            )
    ) {
        ReportMetric.entries.forEach { metric ->
            val isSelected = metric == selectedMetric
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.buttonSecondary
                        } else {
                            MaterialTheme.colorScheme.backgroundWhite
                        }
                    )
                    .clickable { onMetricSelected(metric) }
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = metric.tabIcon(),
                    contentDescription = metric.tabLabel(),
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.buttonPrimary
                    } else {
                        MaterialTheme.colorScheme.textSecondary
                    },
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = metric.tabLabel(),
                    style = MaterialTheme.typography.bodySmallRegular,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.buttonPrimary
                    } else {
                        MaterialTheme.colorScheme.textSecondary
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun MetricTabsPreview() {
    SmartStepTheme {
        MetricTabs(
            selectedMetric = ReportMetric.STEPS,
            onMetricSelected = {}
        )
    }
}
