package com.jvcodingsolutions.smartstep.design_system.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.key.type
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Colors extracted from your design
    val borderColor = Color(0xFFE0E0E0)
    val labelColor = Color(0xFF757575)
    val textColor = Color(0xFF1D1D1D)
    val checkmarkColor = Color(0xFF3F4DB8)
    val selectedBackgroundColor = Color(0xFFF4F5F9)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true) // Critical: Tells the popup where to attach
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.strokeMain,
                    shape = RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.backgroundSecondary)
                .clickable { expanded = true }
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
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .exposedDropdownSize() // Critical: Forces menu to exactly match the anchor's width
                .background(MaterialTheme.colorScheme.backgroundWhite),
            shape = RoundedCornerShape(8.dp),

            offset = DpOffset(0.dp, 8.dp),

        ) {
            options.forEach { option ->
                val isSelected = option == selectedOption

                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.backgroundSecondary
                            else MaterialTheme.colorScheme.backgroundWhite,
                            shape = RoundedCornerShape(10.dp)
                        )
                        ,
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
                                text = option,
                                color = MaterialTheme.colorScheme.textPrimary,
                                style = MaterialTheme.typography.bodyLargeRegular
                            )

                        }
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
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
    var selectedGender by remember { mutableStateOf("Female") }
    val genderOptions = listOf("Female", "Male")
    SmartStepTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
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

                    .padding(16.dp)

            ) {
                SmartStepDropDown(
                    label = "Gender",
                    options = genderOptions,
                    selectedOption = selectedGender,
                    onOptionSelected = { selectedGender = it }
                )
            }
        }
    }
}