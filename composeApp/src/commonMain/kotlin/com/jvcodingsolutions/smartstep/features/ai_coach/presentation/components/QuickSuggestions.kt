package com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.quick_suggestions
import smartstep.composeapp.generated.resources.suggestion_explain_trend
import smartstep.composeapp.generated.resources.suggestion_reach_goal
import smartstep.composeapp.generated.resources.suggestion_recommend_workout

@Composable
fun QuickSuggestions(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSuggestionClick: (String) -> Unit
) {
    // Exactly three fixed suggestions; the visible label doubles as the sent prompt
    val suggestions = listOf(
        stringResource(Res.string.suggestion_recommend_workout),
        stringResource(Res.string.suggestion_explain_trend),
        stringResource(Res.string.suggestion_reach_goal)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable { onToggleExpanded() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.quick_suggestions),
                style = MaterialTheme.typography.bodyLargeMedium,
                color = MaterialTheme.colorScheme.textPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = stringResource(Res.string.quick_suggestions),
                tint = MaterialTheme.colorScheme.textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.backgroundSecondary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.strokeMain,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSuggestionClick(suggestion) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyLargeRegular,
                            color = MaterialTheme.colorScheme.textPrimary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun QuickSuggestionsExpandedPreview() {
    SmartStepTheme {
        QuickSuggestions(
            modifier = Modifier.padding(16.dp),
            isExpanded = true,
            onToggleExpanded = {},
            onSuggestionClick = {}
        )
    }
}

@Preview
@Composable
private fun QuickSuggestionsCollapsedPreview() {
    SmartStepTheme {
        QuickSuggestions(
            modifier = Modifier.padding(16.dp),
            isExpanded = false,
            onToggleExpanded = {},
            onSuggestionClick = {}
        )
    }
}
