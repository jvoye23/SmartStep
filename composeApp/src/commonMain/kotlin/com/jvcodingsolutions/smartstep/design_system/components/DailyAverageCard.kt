package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.buttonSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite
import com.jvcodingsolutions.smartstep.features.step_counter.DailyAverageState
import com.jvcodingsolutions.smartstep.features.step_counter.DayStepData

private fun Int.formatSteps(): String {
    return this.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

@Composable
fun DailyAverageCard(
    state: DailyAverageState,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val cardWidthFraction: Float = when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> 1f
        DeviceConfiguration.MOBILE_LANDSCAPE -> 0.5f
        DeviceConfiguration.TABLET_PORTRAIT -> 0.5f
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> 0.3f
    }

    Surface(
        modifier = modifier.fillMaxWidth(fraction = cardWidthFraction),
        color = MaterialTheme.colorScheme.buttonPrimary,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Daily Average: ${state.dailyAverage.formatSteps()} steps",
                color = MaterialTheme.colorScheme.textWhite,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val spacing = 10.dp
                val totalSpacing = spacing * 6
                val exactItemWidth = (maxWidth - totalSpacing) / 7

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    state.dailySteps.forEach { dayData ->
                        DayStepElement(
                            dayData = dayData,
                            gaugeSize = exactItemWidth
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayStepElement(
    dayData: DayStepData,
    gaugeSize: Dp,
    gaugeStrokeWidth: Dp = 4.dp
) {
    Column(
        modifier = Modifier.width(gaugeSize),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(gaugeSize)
                .padding(bottom = 6.dp)
        ) {
            val stroke = Stroke(width = gaugeStrokeWidth.toPx(), cap = StrokeCap.Round)
            val progressStroke = Stroke(width = (gaugeStrokeWidth / 2).toPx(), cap = StrokeCap.Round)

            // Background ring (empty progress)
            drawArc(
                color = Color.White,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )

            // Active progress ring
            // 270f is exactly 12 o'clock in Compose Canvas.
            // Positive sweepAngle draws clockwise.
            if (dayData.progress > 0f) {
                drawArc(
                    color = Color(0xFF0DC600), // Green progress color
                    startAngle = 270f,
                    sweepAngle = dayData.progress * 360f,
                    useCenter = false,
                    style = progressStroke
                )
            }
        }

        Text(
            text = dayData.dayName,
            color = MaterialTheme.colorScheme.textWhite,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = dayData.steps.formatSteps(),
            color = MaterialTheme.colorScheme.buttonSecondary,
            fontSize = 11.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DailyAverageCardPreview() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DailyAverageCard(
            state = DailyAverageState(
                dailyAverage = 5125,
                dailySteps = listOf(
                    DayStepData("Sun", 12345, 0.5f),
                    DayStepData("Mon", 2000, 0.7f),
                    DayStepData("Tue", 3000, 0.9f),
                    DayStepData("Wed", 4000, 1f),
                    DayStepData("Thu", 5000, 0.8f),
                    DayStepData("Fri", 6000, 0.6f),
                    DayStepData("Sat", steps = 4500, progress = 0.4f)
                )
            )
        )
    }
}