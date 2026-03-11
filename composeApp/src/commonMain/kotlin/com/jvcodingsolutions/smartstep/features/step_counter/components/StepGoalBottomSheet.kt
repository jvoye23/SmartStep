@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.features.step_counter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.components.SmartStepFilledButton
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundMain
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundTertiary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.cancel
import smartstep.composeapp.generated.resources.save
import smartstep.composeapp.generated.resources.step_goal
import kotlin.math.abs

@Composable
fun StepGoalBottomSheet(
    modifier: Modifier = Modifier,
    initialValue: Int = 2000,
    onSave: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val stepValues = remember {
        (40000 downTo 1000 step 1000).toList()
    }
    var currentSelectedValue by rememberSaveable { mutableStateOf(initialValue) }

    ModalBottomSheet(
        onDismissRequest = { onCancel() },
        modifier = modifier.fillMaxWidth(),
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.backgroundSecondary,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.backgroundSecondary)
                    .padding(all = 16.dp)
                    ,
                ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.step_goal),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                StepGoalPicker(
                    values = stepValues,
                    selectedValue = currentSelectedValue,
                    onValueChange = { currentSelectedValue = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                SmartStepFilledButton(
                    onClick = {
                        onSave(currentSelectedValue)
                        onCancel()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    buttonText = stringResource(Res.string.save),
                )
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.cancel),
                        style = MaterialTheme.typography.bodyLargeMedium,
                        color = MaterialTheme.colorScheme.buttonPrimary

                    )

                }
            }
        }
    }

}

@Composable
private fun StepGoalPicker(
    values: List<Int>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemHeightDp = 50.dp
    // 4 items visible exactly: 4 * 50dp = 200dp
    val pickerHeight = 200.dp

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = values.indexOf(selectedValue).coerceAtLeast(0)
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    val centeredItemIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf 0

            val viewportCenter = layoutInfo.viewportStartOffset + (layoutInfo.viewportSize.height / 2)

            visibleItems.minByOrNull { itemInfo ->
                abs((itemInfo.offset + itemInfo.size / 2) - viewportCenter)
            }?.index ?: 0
        }
    }

    LaunchedEffect(centeredItemIndex, listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && centeredItemIndex in values.indices) {
            onValueChange(values[centeredItemIndex])
        }
    }
    Box(
        modifier = modifier.height(pickerHeight),
        contentAlignment = Alignment.Center
    ) {
        // --- Selection Indicator ---
        // Styled with rounded corners and your custom background color to match the design
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.backgroundTertiary)
        )

        // --- The Scrollable List ---
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                // This padding allows the first and last items to reach the vertical center
                top = (pickerHeight - itemHeightDp) / 2,
                bottom = (pickerHeight - itemHeightDp) / 2
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(values.size) { index ->
                val value = values[index]
                val isSelected = centeredItemIndex == index

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp)
                        .clickable(
                            // Optional: Remove ripple effect for a cleaner iOS-like picker feel
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.textPrimary
                            else MaterialTheme.colorScheme.textSecondary,
                            modifier = Modifier.alpha(
                                if (isSelected) 1f else {
                                    // Beautiful distance-based fade out
                                    val distance = abs(index - centeredItemIndex)
                                    (1f - distance * 0.3f).coerceAtLeast(0.3f)
                                }
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun StepCounterScreenPreview() {
    SmartStepTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StepGoalBottomSheet(
                onSave = {},
                onCancel = {}
            )

        }
    }
}