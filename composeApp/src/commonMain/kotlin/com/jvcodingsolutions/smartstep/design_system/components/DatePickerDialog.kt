package com.jvcodingsolutions.smartstep.design_system.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jvcodingsolutions.smartstep.core.presentation.util.DeviceConfiguration
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundTertiary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeMedium
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textSecondary
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.cancel
import smartstep.composeapp.generated.resources.save
import kotlin.math.abs
import kotlin.time.Clock

@Composable
fun DatePickerDialog(
    initialDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    title: String = "Date"
) {
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val effectiveInitialDate = initialDate ?: LocalDate(1990, 1, 1)

    var selectedYear by rememberSaveable { mutableStateOf(effectiveInitialDate.year) }
    var selectedMonth by rememberSaveable { mutableStateOf(effectiveInitialDate.month.number) }
    var selectedDay by rememberSaveable { mutableStateOf(effectiveInitialDate.day) }

    val years = remember { (1900..today.year).toList() }
    val months = remember { (1..12).toList() }
    
    // Dynamic days list based on year and month
    val days = remember(selectedYear, selectedMonth) {
        val lastDay = when (selectedMonth) {
            4, 6, 9, 11 -> 30
            2 -> {
                val isLeap = (selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0)
                if (isLeap) 29 else 28
            }
            else -> 31
        }
        (1..lastDay).toList()
    }

    // Adjust day if it exceeds the maximum days of the selected month
    LaunchedEffect(days) {
        if (selectedDay > days.size) {
            selectedDay = days.size
        }
    }

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = true
        ),
        onDismissRequest = onDismiss
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val deviceConfiguration = DeviceConfiguration.fromWindowSizeClass(windowSizeClass)

        val deviceWidthFraction: Float = when(deviceConfiguration) {
            DeviceConfiguration.TABLET_PORTRAIT,
            DeviceConfiguration.TABLET_LANDSCAPE,
            DeviceConfiguration.DESKTOP -> 0.6f
            else -> 1f
        }

        Surface(
            modifier = Modifier.fillMaxWidth(fraction = deviceWidthFraction),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.backgroundSecondary
        ) {
            Column(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                // Header
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Picker Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Selection Indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(MaterialTheme.colorScheme.backgroundTertiary)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DatePickerColumn(
                            values = years,
                            selectedValue = selectedYear,
                            onValueChange = { selectedYear = it },
                            modifier = Modifier.weight(1.2f)
                        )
                        DatePickerColumn(
                            values = months,
                            selectedValue = selectedMonth,
                            onValueChange = { selectedMonth = it },
                            modifier = Modifier.weight(1f)
                        )
                        DatePickerColumn(
                            values = days,
                            selectedValue = selectedDay,
                            onValueChange = { selectedDay = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            try {
                                onConfirm(LocalDate(selectedYear, selectedMonth, selectedDay))
                            } catch (e: Exception) {
                                onConfirm(effectiveInitialDate)
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.save),
                            style = MaterialTheme.typography.bodyLargeMedium,
                            color = MaterialTheme.colorScheme.buttonPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerColumn(
    values: List<Int>,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeightDp = 50.dp
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

            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2

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

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = (pickerHeight - itemHeightDp) / 2,
            bottom = (pickerHeight - itemHeightDp) / 2
        ),
        modifier = modifier.height(pickerHeight)
    ) {
        items(values.size) { index ->
            val value = values[index]
            val isSelected = centeredItemIndex == index

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.textPrimary 
                            else MaterialTheme.colorScheme.textSecondary,
                    modifier = Modifier.alpha(
                        if (isSelected) 1f else {
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

@Preview
@Composable
private fun DatePickerDialogPreview() {
    SmartStepTheme {
        DatePickerDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}
