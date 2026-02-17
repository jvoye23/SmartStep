package com.jvcodingsolutions.smartstep

import com.jvcodingsolutions.smartstep.design_system.components.Height
import com.jvcodingsolutions.smartstep.design_system.components.HeightPickerDialog
import com.jvcodingsolutions.smartstep.design_system.components.Weight
import com.jvcodingsolutions.smartstep.design_system.components.WeightPickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.components.SmartStepDropDown
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain

@Composable
@Preview
fun App() {

    SmartStepTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.backgroundSecondary)
                .safeContentPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var uiState by remember { mutableStateOf(HeightPickerState(selectedHeight = 175)) }

            SmartStepDropDownExample()


        }
    }
}

data class HeightPickerState(
    val selectedHeight: Int = 175,
    val isPickerVisible: Boolean = false
)

@Composable
fun HeightPickerExample() {
    var showDialog by remember { mutableStateOf(false) }
    var currentHeight by remember { mutableStateOf(Height(cm = 175)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDialog = true }) {
            Text("Select height")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Selected Height: ${currentHeight.toCm()} cm")
    }

    if (showDialog) {
        HeightPickerDialog(
            initialHeight = currentHeight,
            onDismiss = { showDialog = false },
            onConfirm = { height ->
                currentHeight = height
                showDialog = false
            }
        )
    }
}

@Composable
fun WeightPickerExample() {
    var showDialog by remember { mutableStateOf(false) }
    var currentWeight by remember { mutableStateOf(Weight(kg = 175)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDialog = true }) {
            Text("Select Weight")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Current Weight: $currentWeight")
    }

    if (showDialog) {
        WeightPickerDialog(
            initialWeight = Weight(kg = 75),
            onDismiss = {showDialog = false},
            onConfirm = { weight ->
                currentWeight = weight
                showDialog = false

            }
        )
    }
}

@Composable
private fun SmartStepDropDownExample() {
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