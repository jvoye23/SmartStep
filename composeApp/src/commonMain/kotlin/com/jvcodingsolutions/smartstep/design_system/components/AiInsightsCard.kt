package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_AI
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_ArrowRight
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Refresh
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumMedium
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.buttonSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.ai_insights_error
import smartstep.composeapp.generated.resources.ai_insights_offline
import smartstep.composeapp.generated.resources.more
import smartstep.composeapp.generated.resources.try_again

@Composable
fun AiInsightsCard(
    modifier: Modifier = Modifier,
    insight: String?,
    isLoading: Boolean,
    isOffline: Boolean,
    isError: Boolean = false,
    onMoreClick: () -> Unit,
    onTryAgainClick: () -> Unit
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

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.backgroundWhite),
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
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.buttonSecondary,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icon_AI,
                        contentDescription = "AI Insights Icon",
                        tint = MaterialTheme.colorScheme.buttonPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isOffline || isError) {
                    Row(
                        modifier = Modifier.clickable { onTryAgainClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.try_again),
                            style = MaterialTheme.typography.bodyMediumMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icon_Refresh,
                            contentDescription = stringResource(Res.string.try_again),
                            tint = MaterialTheme.colorScheme.buttonPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.clickable { onMoreClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(Res.string.more),
                            style = MaterialTheme.typography.bodyMediumMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icon_ArrowRight,
                            contentDescription = stringResource(Res.string.more),
                            tint = MaterialTheme.colorScheme.buttonPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isOffline -> {
                    Text(
                        text = stringResource(Res.string.ai_insights_offline),
                        style = MaterialTheme.typography.bodyLargeRegular,
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }
                isError -> {
                    Text(
                        text = stringResource(Res.string.ai_insights_error),
                        style = MaterialTheme.typography.bodyLargeRegular,
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }
                isLoading && insight == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.buttonPrimary,
                        strokeWidth = 2.dp
                    )
                }
                else -> {
                    Text(
                        text = insight.orEmpty(),
                        style = MaterialTheme.typography.bodyLargeRegular,
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AiInsightsCardPreview() {
    SmartStepTheme {
        AiInsightsCard(
            insight = "You are slightly behind today's pace — 1.2k steps needed.",
            isLoading = false,
            isOffline = false,
            onMoreClick = {},
            onTryAgainClick = {}
        )
    }
}

@Preview
@Composable
private fun AiInsightsCardOfflinePreview() {
    SmartStepTheme {
        AiInsightsCard(
            insight = null,
            isLoading = false,
            isOffline = true,
            onMoreClick = {},
            onTryAgainClick = {}
        )
    }
}

@Preview
@Composable
private fun AiInsightsCardLoadingPreview() {
    SmartStepTheme {
        AiInsightsCard(
            insight = null,
            isLoading = true,
            isOffline = false,
            onMoreClick = {},
            onTryAgainClick = {}
        )
    }
}

@Preview
@Composable
private fun AiInsightsCardErrorPreview() {
    SmartStepTheme {
        AiInsightsCard(
            insight = null,
            isLoading = false,
            isOffline = false,
            isError = true,
            onMoreClick = {},
            onTryAgainClick = {}
        )
    }
}
