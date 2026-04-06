package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_ArrowDown
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_ArrowRight
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_ArrowUp
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Sneakers
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Steps
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_WeightScale
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundMain
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite
import com.jvcodingsolutions.smartstep.design_system.util.formattedSteps
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.smart_step_counter


@Composable
fun SmartStepNotification(
    modifier: Modifier = Modifier,
    currentSteps: Int,
    dailyGoalSteps: Int,
    calories: Int,
    onClick: () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(true) }

    val progress = remember(currentSteps, dailyGoalSteps) {
        if (dailyGoalSteps > 0) {
            (currentSteps.toFloat() / dailyGoalSteps.toFloat()).coerceIn(0f, 1f)
        } else 0f
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.backgroundSecondary
            )
            // 1. Smoothly animates the height changes of the Row
            .animateContentSize(animationSpec = spring())
            // 2. Forces the Row (and its side columns) to match the height of the middle content
            .height(IntrinsicSize.Min)
            .padding(16.dp)
    ) {
        // --- COLUMN 1: LEADING ICON ---
        // Animates vertical position from 0f (Center) to -1f (Top)
        val leadingIconBias by animateFloatAsState(
            targetValue = if (isExpanded) -1f else 0f,
            label = "leadingIconBias"
        )



        // --- COLUMN 2: MIDDLE CONTENT ---
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Header Text (Expands and fades in)
            this@Column.AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

            }

            // Persistent Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconLabel(
                    icon = Icon_Sneakers,
                    label = formattedSteps(currentSteps)
                )
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(15.dp),
                    color = MaterialTheme.colorScheme.strokeMain
                )
                IconLabel(
                    icon = Icon_WeightScale,
                    label = calories.toString()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.buttonPrimary,
                trackColor = MaterialTheme.colorScheme.backgroundMain,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }

        // --- COLUMN 3: TRAILING ACTIONS ---
        // Animates the toggle arrow exactly like the leading icon
        val trailingToggleBias by animateFloatAsState(
            targetValue = if (isExpanded) -1f else 0f,
            label = "trailingToggleBias"
        )

        Box(
            modifier = Modifier.fillMaxHeight()
        ) {

            // Action/Navigate Button (Fades in at the bottom)
            this@Row.AnimatedVisibility(
                visible = isExpanded,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.buttonPrimary)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icon_ArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.textWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun IconLabel(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.buttonPrimary,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLargeMedium,
            color = MaterialTheme.colorScheme.textPrimary
        )
    }
}

@Preview
@Composable
fun SmartStepNotificationPreview() {
    SmartStepTheme {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            SmartStepNotification(
                currentSteps = 1000,
                calories = 2000,
                dailyGoalSteps = 3000,
                onClick = {}
            )

        }

    }
}
