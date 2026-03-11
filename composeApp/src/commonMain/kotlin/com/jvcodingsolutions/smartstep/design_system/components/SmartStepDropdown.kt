package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.core.domain.model.Gender
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_ArrowDown
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Selected
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.bodySmallRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartStepDropDown(
    label: String,
    options: List<String>? = null,
    expanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    selectedOption: String,
    onOptionSelected: (Gender) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color
) {

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { !expanded },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.strokeMain,
                    shape = RoundedCornerShape(10.dp))
                .background(containerColor)
                .clickable { onExpandedChange(expanded) }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.textSecondary,
                    style = MaterialTheme.typography.bodySmallRegular
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = selectedOption,
                    color = MaterialTheme.colorScheme.textPrimary,
                    style = MaterialTheme.typography.bodyLargeRegular
                )
            }

            Icon(
                imageVector = Icon_ArrowDown,
                contentDescription = "Expand menu",
                tint = MaterialTheme.colorScheme.textPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { /*onExpandedChange(expanded) */}, // the dialog shall not dismiss when tapping outside
            modifier = Modifier
                .exposedDropdownSize() // Critical: Forces menu to exactly match the anchor's width
                .background(MaterialTheme.colorScheme.backgroundWhite),
            shape = RoundedCornerShape(8.dp),

            offset = DpOffset(0.dp, 8.dp),

        ) {
            Gender.entries.forEach { gender ->
                val isSelected = gender.label == selectedOption



                /*}
                options?.forEach { option ->
                    val isSelected = option == selectedOption*/

                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.backgroundSecondary
                            else MaterialTheme.colorScheme.backgroundWhite,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentPadding = PaddingValues(
                        start = 8.dp, top = 8.dp, end = 4.dp, bottom = 8.dp
                    ),
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = gender.label,
                                color = MaterialTheme.colorScheme.textPrimary,
                                style = MaterialTheme.typography.bodyLargeRegular
                            )

                        }
                    },
                    onClick = {
                        onOptionSelected(gender)
                        onExpandedChange(expanded)
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icon_Selected,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.buttonPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun StepCounterCardPreview() {
    var selectedGender by remember { mutableStateOf(Gender.FEMALE) }
    val genderOptions = listOf("Female", "Male")
    SmartStepTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.backgroundWhite
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.strokeMain,
                        shape = RoundedCornerShape(14.dp)
                    )

                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                SmartStepDropDown(
                    label = "Gender",
                    options = genderOptions,
                    selectedOption = selectedGender.label,
                    onOptionSelected = { selectedGender = it },
                    containerColor = MaterialTheme.colorScheme.backgroundSecondary
                )
                SmartStepDropDown(
                    label = "Gender",
                    options = genderOptions,
                    selectedOption = selectedGender.label,
                    onOptionSelected = { selectedGender = it },
                    containerColor = MaterialTheme.colorScheme.backgroundSecondary
                )
            }
        }
    }
}