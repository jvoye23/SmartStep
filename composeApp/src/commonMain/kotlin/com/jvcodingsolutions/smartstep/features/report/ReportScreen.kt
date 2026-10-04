@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.features.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jvcodingsolutions.smartstep.core.presentation.util.isWideScreenLayout
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.features.report.components.MetricTabs
import com.jvcodingsolutions.smartstep.features.report.components.ReportDayItem
import com.jvcodingsolutions.smartstep.features.report.components.ReportSummaryCard
import com.jvcodingsolutions.smartstep.features.report.components.WeekSelector
import com.jvcodingsolutions.smartstep.features.report.components.dayUnit
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.navigate_back
import smartstep.composeapp.generated.resources.report

@Composable
fun ReportScreenRoot(
    viewModel: ReportViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReportScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ReportAction.OnBackClick -> onNavigateBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ReportScreen(
    state: ReportState,
    onAction: (ReportAction) -> Unit
) {
    val isWideScreen = isWideScreenLayout()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.backgroundSecondary,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onAction(ReportAction.OnBackClick) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.navigate_back),
                                tint = MaterialTheme.colorScheme.textPrimary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(Res.string.report),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.textPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.backgroundSecondary
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.strokeMain)
            }
        },
        bottomBar = {
            // Fixed to the bottom, never scrolls with the content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.backgroundSecondary),
                contentAlignment = Alignment.Center
            ) {
                MetricTabs(
                    modifier = if (isWideScreen) Modifier.width(394.dp) else Modifier,
                    selectedMetric = state.selectedMetric,
                    onMetricSelected = { onAction(ReportAction.OnMetricSelected(it)) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ReportSummaryCard(
                modifier = if (isWideScreen) {
                    Modifier.width(394.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                },
                metric = state.selectedMetric,
                isMetricSystem = state.isMetricSystem,
                isCurrentWeek = state.isCurrentWeek,
                weeklyTotal = state.weeklyTotal,
                dailyAverage = state.dailyAverage
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeekSelector(
                modifier = if (isWideScreen) Modifier.width(394.dp) else Modifier.fillMaxWidth(),
                weekRangeLabel = state.weekRangeLabel,
                isNextWeekEnabled = state.isNextWeekEnabled,
                onPreviousWeekClick = { onAction(ReportAction.OnPreviousWeekClick) },
                onNextWeekClick = { onAction(ReportAction.OnNextWeekClick) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Only the day list scrolls; summary card, week selector and tabs stay fixed
            if (isWideScreen) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 600.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = state.dayItems.size,
                        key = { index -> state.dayItems[index].date.toEpochDays() }
                    ) { index ->
                        DayItem(state = state, day = state.dayItems[index])
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = state.dayItems.size,
                        key = { index -> state.dayItems[index].date.toEpochDays() }
                    ) { index ->
                        DayItem(state = state, day = state.dayItems[index])
                    }
                }
            }
        }
    }
}

@Composable
private fun DayItem(state: ReportState, day: ReportDayUi) {
    ReportDayItem(
        dayName = day.dayName,
        value = day.value,
        unit = state.selectedMetric.dayUnit(state.isMetricSystem),
        status = day.status,
        goalSteps = day.goalSteps,
        isToday = day.isToday
    )
}

private fun previewState() = ReportState(
    selectedMetric = ReportMetric.STEPS,
    weekRangeLabel = "Nov 16 – Nov 22",
    isNextWeekEnabled = false,
    weeklyTotal = "573",
    dailyAverage = "191",
    dayItems = listOf(
        ReportDayUi(LocalDate(2025, 11, 16), "Monday", "245", DayStatus.DONE, 6000, false),
        ReportDayUi(LocalDate(2025, 11, 17), "Tuesday", "328", DayStatus.DONE, 6000, false),
        ReportDayUi(LocalDate(2025, 11, 18), "Wednesday", "0", DayStatus.DONE, 6000, false),
        ReportDayUi(LocalDate(2025, 11, 19), "Thursday", "0", DayStatus.IN_PROGRESS, 6000, true),
        ReportDayUi(LocalDate(2025, 11, 20), "Friday", "0", DayStatus.NO_DATA, null, false),
        ReportDayUi(LocalDate(2025, 11, 21), "Saturday", "0", DayStatus.NO_DATA, null, false),
        ReportDayUi(LocalDate(2025, 11, 22), "Sunday", "0", DayStatus.NO_DATA, null, false)
    )
)

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ReportScreenPreview() {
    SmartStepTheme {
        ReportScreen(
            state = previewState(),
            onAction = {}
        )
    }
}

@Preview(widthDp = 900, heightDp = 900)
@Composable
private fun ReportScreenWidePreview() {
    SmartStepTheme {
        ReportScreen(
            state = previewState(),
            onAction = {}
        )
    }
}
