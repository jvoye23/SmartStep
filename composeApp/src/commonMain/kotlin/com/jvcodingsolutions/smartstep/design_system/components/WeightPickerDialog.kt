package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.jvcodingsolutions.smartstep.core.domain.WeightUnit
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundTertiary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.cancel
import smartstep.composeapp.generated.resources.ok
import smartstep.composeapp.generated.resources.used_to_calculate_calories
import smartstep.composeapp.generated.resources.weight
import kotlin.math.abs


data class Weight(
    val kg: Int? = null,
    val lbs: Int? = null
) {
    fun toLbs(): Int {
        return kg?.times(2.20462.toInt()) ?: 999
    }

    fun toKg(): Int {
        return lbs?.div(2.20462)?.toInt() ?: 999
    }
}

@Composable
fun WeightPickerDialog(
    initialWeight: Weight = Weight(kg = 65, lbs = 143),
    onDismiss: () -> Unit,
    onConfirm: (Weight) -> Unit
) {
    var selectedUnit by remember { mutableStateOf(WeightUnit.KG) }
    var selectedKg by remember { mutableStateOf(initialWeight.kg ?: 65) }
    var selectedLbs by remember { mutableStateOf(initialWeight.lbs ?: 143) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.backgroundSecondary
        ) {
            Column(
            ) {
                // Header
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 24.dp),
                    text = stringResource(Res.string.weight),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    text = stringResource(Res.string.used_to_calculate_calories),
                    style = MaterialTheme.typography.bodyMediumRegular,
                    color = MaterialTheme.colorScheme.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SmartStepSegmentedButton(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    options = WeightUnit.entries,
                    selectedUnit = selectedUnit,
                    onUnitChange = { selectedUnit = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Picker Content
                AnimatedContent(
                    targetState = selectedUnit,
                    modifier = Modifier.height(200.dp)
                ) { unit ->
                    when (unit) {
                        WeightUnit.KG -> {
                            WeightPicker(
                                selectedValue = selectedKg,
                                onValueChange = { selectedKg = it },
                                weightUnit = WeightUnit.KG
                            )
                        }
                        WeightUnit.LBS -> {
                            WeightPicker(
                                selectedValue =  selectedLbs,
                                onValueChange = { selectedLbs = it},
                                weightUnit = WeightUnit.LBS
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    TextButton(
                        onClick = {
                            val weight = when (selectedUnit) {
                                WeightUnit.KG -> Weight(kg = selectedKg)
                                WeightUnit.LBS -> Weight(lbs = selectedLbs)
                            }
                            onConfirm(weight)
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.ok),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun WeightPicker(
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    weightUnit: WeightUnit
) {
    val values = if(weightUnit == WeightUnit.KG) (50..150).toList() else
        (100..350).toList()
    WeightNumberPicker(
        values = values,
        selectedValue = selectedValue,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun WeightNumberPicker(
    values: List<Int>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    val itemHeightDp = 50.dp
    val pickerHeight = 200.dp

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = values.indexOf(selectedValue).coerceAtLeast(0)
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    // Calculate which item is currently centered
    val centeredItemIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2

            layoutInfo.visibleItemsInfo
                .minByOrNull { itemInfo ->
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
        // Selection Indicator - fixed in center
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .background(MaterialTheme.colorScheme.backgroundTertiary)
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
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
                        .clickable {
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
                            color = if(isSelected) MaterialTheme.colorScheme.textPrimary else
                                MaterialTheme.colorScheme.textSecondary,
                            modifier = Modifier.alpha(
                                if (isSelected) 1f else {
                                    val distance = abs(index - centeredItemIndex)
                                    (1f - distance * 0.3f).coerceAtLeast(0.3f)
                                }
                            ),
                            textAlign = TextAlign.Center
                        )

                        if (suffix != null && isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = suffix,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.textPrimary,
                                modifier = Modifier.alpha(0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview
@Composable
private fun WeightPickerDialogPreview(){
    SmartStepTheme {
        WeightPickerDialog(
            initialWeight = Weight(65),
            onDismiss = {},
            onConfirm = {}
        )
    }
}