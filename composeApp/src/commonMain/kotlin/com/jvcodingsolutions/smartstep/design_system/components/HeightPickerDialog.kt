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
import com.jvcodingsolutions.smartstep.core.domain.HeightUnit
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
import smartstep.composeapp.generated.resources.height
import smartstep.composeapp.generated.resources.ok
import smartstep.composeapp.generated.resources.used_to_calculate_distance
import kotlin.math.abs


data class Height(
    val cm: Int? = null,
    val feet: Int? = null,
    val inches: Int? = null
) {
    fun toCm(): Int {
        return cm ?: ((feet ?: 0) * 30.48 + (inches ?: 0) * 2.54).toInt()
    }

    fun toFeetInches(): Pair<Int, Int> {
        val totalCm = cm ?: ((feet ?: 0) * 30.48 + (inches ?: 0) * 2.54).toInt()
        val totalInches = (totalCm / 2.54).toInt()
        return Pair(totalInches / 12, totalInches % 12)
    }
}

@Composable
fun HeightPickerDialog(
    initialHeight: Height = Height(cm = 175),
    onDismiss: () -> Unit,
    onConfirm: (Height) -> Unit
) {
    var selectedUnit by remember { mutableStateOf(HeightUnit.CM) }
    var selectedCm by remember { mutableStateOf(initialHeight.cm ?: 175) }
    var selectedFeet by remember {
        mutableStateOf(initialHeight.feet ?: initialHeight.toFeetInches().first)
    }
    var selectedInches by remember {
        mutableStateOf(initialHeight.inches ?: initialHeight.toFeetInches().second)
    }

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
                    text = stringResource(Res.string.height),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    text = stringResource(Res.string.used_to_calculate_distance),
                    style = MaterialTheme.typography.bodyMediumRegular,
                    color = MaterialTheme.colorScheme.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                /*SmartStepSegmentedButton(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    options = listOf(HeightUnit.CM, HeightUnit.FT_IN),
                    selectedUnit = selectedUnit,
                    onUnitChange = { selectedUnit = it },
                )*/
                SmartStepSegmentedButton(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    options = HeightUnit.entries,
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
                        HeightUnit.CM -> {
                            CmPicker(
                                selectedCm = selectedCm,
                                onCmChange = { selectedCm = it }
                            )
                        }
                        HeightUnit.FT_IN -> {
                            FeetInchesPicker(
                                selectedFeet = selectedFeet,
                                selectedInches = selectedInches,
                                onFeetChange = { selectedFeet = it },
                                onInchesChange = { selectedInches = it }
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
                            val height = when (selectedUnit) {
                                HeightUnit.CM -> Height(cm = selectedCm)
                                HeightUnit.FT_IN -> Height(feet = selectedFeet, inches = selectedInches)
                            }
                            onConfirm(height)
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
fun CmPicker(
    selectedCm: Int,
    onCmChange: (Int) -> Unit
) {
    val values = (100..250).toList()

    NumberPicker(
        values = values,
        selectedValue = selectedCm,
        onValueChange = onCmChange,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FeetInchesPicker(
    selectedFeet: Int,
    selectedInches: Int,
    onFeetChange: (Int) -> Unit,
    onInchesChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Feet Picker
        NumberPicker(
            values = (3..7).toList(),
            selectedValue = selectedFeet,
            onValueChange = onFeetChange,
            modifier = Modifier.weight(1f),
            suffix = "ft"
        )

        // Inches Picker
        NumberPicker(
            values = (0..11).toList(),
            selectedValue = selectedInches,
            onValueChange = onInchesChange,
            modifier = Modifier.weight(1f),
            suffix = "in"
        )
    }
}

@Composable
fun NumberPicker(
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
private fun HeightPickerDialog(){
    SmartStepTheme {
        HeightPickerDialog(
            initialHeight = Height(175),
            onDismiss = {  },
            onConfirm = {}
        )
    }
}