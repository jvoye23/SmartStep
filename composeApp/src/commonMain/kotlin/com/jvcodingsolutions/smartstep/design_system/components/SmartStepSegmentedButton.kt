package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jvcodingsolutions.smartstep.core.domain.HeightUnit
import com.jvcodingsolutions.smartstep.core.domain.UnitWithLabel
import com.jvcodingsolutions.smartstep.core.domain.WeightUnit
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Selected
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumMedium
import com.jvcodingsolutions.smartstep.design_system.theme.buttonSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary

@Composable
fun <T> SmartStepSegmentedButton(
    modifier: Modifier = Modifier,
    options: List<T>,
    selectedUnit: T,
    onUnitChange: (T) -> Unit
) where T : Enum<T>, T : UnitWithLabel {
    val checkedUnit = remember { mutableStateOf(selectedUnit) }

    MultiChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        options.forEachIndexed { index, unit ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    SegmentedButtonDefaults.Icon(active = unit == selectedUnit) {
                        Icon(
                            imageVector = Icon_Selected,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.textPrimary.copy(alpha =
                                if(unit == selectedUnit) 1f else 0f),
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                },
                onCheckedChange = {
                    checkedUnit.value = unit
                    onUnitChange(checkedUnit.value)
                },
                checked = checkedUnit.value == unit,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.buttonSecondary,
                    inactiveContentColor = MaterialTheme.colorScheme.backgroundSecondary,
                    activeContentColor = MaterialTheme.colorScheme.textPrimary,
                    activeBorderColor = MaterialTheme.colorScheme.strokeMain,
                    inactiveBorderColor = MaterialTheme.colorScheme.strokeMain,
                )
            ) {
                Text(
                    text = unit.label,
                    style = MaterialTheme.typography.bodyMediumMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }
        }
    }
}



/*@Composable
fun SmartStepSegmentedButton(
    modifier: Modifier = Modifier,
    options: List<HeightUnit>,
    selectedUnit: HeightUnit,
    onUnitChange: (HeightUnit) -> Unit
) {
    val checkedUnit = remember { mutableStateOf<HeightUnit>(selectedUnit) }

    MultiChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        options.forEachIndexed { index, heightUnit ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                icon = {
                    SegmentedButtonDefaults.Icon(active = heightUnit == selectedUnit) {
                        Icon(
                            imageVector = Icon_Selected,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.textPrimary.copy(alpha =
                                if(heightUnit == selectedUnit) 1f else 0f),
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                },
                onCheckedChange = {
                    checkedUnit.value = heightUnit
                    onUnitChange(checkedUnit.value)
                },
                checked = checkedUnit.value == heightUnit,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.buttonSecondary,
                    inactiveContentColor = MaterialTheme.colorScheme.backgroundSecondary,
                    activeContentColor = MaterialTheme.colorScheme.textPrimary,
                    activeBorderColor = MaterialTheme.colorScheme.strokeMain,
                    inactiveBorderColor = MaterialTheme.colorScheme.strokeMain,

                )
            ) {
                Text(
                    text = heightUnit.label,
                    style = MaterialTheme.typography.bodyMediumMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }
        }
    }
}*/

@Preview(showBackground = true, showSystemUi = true)

@Composable
fun SmartStepSegmentedButtonPreview() {
    SmartStepTheme {
        /*SmartStepSegmentedButton(
            options = listOf(HeightUnit.CM, HeightUnit.FT_IN),
            selectedUnit = HeightUnit.CM,
            onUnitChange = {},
        )*/
        // For HeightUnit
        SmartStepSegmentedButton(
            options = HeightUnit.entries,
            selectedUnit = HeightUnit.CM,
            onUnitChange = { newUnit -> /* handle change */ }
        )

// For WeightUnit
        SmartStepSegmentedButton(
            options = WeightUnit.entries,
            selectedUnit = WeightUnit.KG,
            onUnitChange = { newUnit -> /* handle change */ }
        )
    }
}