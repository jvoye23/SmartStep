package com.jvcodingsolutions.smartstep.features.report

import com.jvcodingsolutions.smartstep.core.presentation.util.startOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class ReportState(
    val isMetricSystem: Boolean = true,
    val selectedMetric: ReportMetric = ReportMetric.STEPS,
    val weekStart: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.startOfWeek(),
    val weekRangeLabel: String = "",
    val isNextWeekEnabled: Boolean = false,
    val isCurrentWeek: Boolean = true,
    val weeklyTotal: String = "0",
    val dailyAverage: String = "0",
    val dayItems: List<ReportDayUi> = emptyList()
)

data class ReportDayUi(
    val date: LocalDate,
    val dayName: String,
    val value: String,
    val status: DayStatus,
    // Non-null only when the Goal label should be shown (Steps metric, day with data)
    val goalSteps: Int?,
    val isToday: Boolean
)
