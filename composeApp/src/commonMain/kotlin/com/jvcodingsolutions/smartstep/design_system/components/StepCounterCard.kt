package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.core.presentation.util.formatActivityDuration
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Clock
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Pause
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_PenEdit
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_PinLocation
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Play
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Sneakers
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_WeightScale
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite
import com.jvcodingsolutions.smartstep.design_system.theme.titleAccent
import com.jvcodingsolutions.smartstep.design_system.util.formattedSteps
import kotlin.time.Duration

@Composable
fun StepCounterCard(
    modifier: Modifier = Modifier,
    currentSteps: Int,
    dailyGoalSteps: Int,
    isMetricSystem: Boolean = true,
    distance: String = "0.0",
    kcal: Int = 0,
    duration: Duration = Duration.ZERO,
    isPaused: Boolean = false,
    onEditClick: () -> Unit,
    togglePlayPause: () -> Unit,
) {
    val currentFormattedSteps = remember(currentSteps) {
        formattedSteps(currentSteps)
    }

    val progress = remember(currentSteps, dailyGoalSteps) {
        if (dailyGoalSteps > 0) {
            (currentSteps.toFloat() / dailyGoalSteps.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

    val cardWidthFraction: Float = when(deviceConfiguration) {
        DeviceConfiguration.MOBILE_PORTRAIT -> 1f
        DeviceConfiguration.MOBILE_LANDSCAPE -> 0.5f
        DeviceConfiguration.TABLET_PORTRAIT -> 0.5f
        DeviceConfiguration.TABLET_LANDSCAPE,
        DeviceConfiguration.DESKTOP -> 0.3f
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.buttonPrimary),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp),
        modifier = modifier.fillMaxWidth(fraction = cardWidthFraction)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icon_Sneakers,
                        contentDescription = "Steps Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onEditClick() },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(50.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Icon(
                        imageVector = Icon_PenEdit,
                        contentDescription = "Steps Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { togglePlayPause() },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    shape = RoundedCornerShape(50.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Icon(
                        imageVector = if(!isPaused) Icon_Play else Icon_Pause,
                        contentDescription = "Steps Icon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentFormattedSteps,
                color = if(isPaused) MaterialTheme.colorScheme.textWhite.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.textWhite,
                style = MaterialTheme.typography.titleAccent
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if(isPaused)"Paused" else "/$dailyGoalSteps Steps",
                color = MaterialTheme.colorScheme.textWhite,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.backgroundWhite.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color.White,
                    trackColor = Color.Transparent,
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icon_PinLocation,
                            contentDescription = "Steps Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom

                    ) {
                        Text(
                            text = distance,
                            color = MaterialTheme.colorScheme.textWhite,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if(isMetricSystem) "km" else "mi",
                            color = MaterialTheme.colorScheme.textWhite,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )

                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icon_WeightScale,
                            contentDescription = "Steps Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom

                    ) {
                        Text(
                            text = "$kcal",
                            color = MaterialTheme.colorScheme.textWhite,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "kcal",
                            color = MaterialTheme.colorScheme.textWhite,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )

                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icon_Clock,
                            contentDescription = "Steps Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom

                    ) {
                        Text(
                            text = formatActivityDuration(duration).split(" ")[0],
                            color = MaterialTheme.colorScheme.textWhite,
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "min",
                            color = MaterialTheme.colorScheme.textWhite,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )

                    }
                }
            }


        }
    }
}
@Preview
@Composable
private fun StepCounterCardPreview() {
    SmartStepTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            StepCounterCard(
                currentSteps = 4523,
                dailyGoalSteps = 6000,
                onEditClick = {},
                togglePlayPause = {}
            )
        }
    }
}