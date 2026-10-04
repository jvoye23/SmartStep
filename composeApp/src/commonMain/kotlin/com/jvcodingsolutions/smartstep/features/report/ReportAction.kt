package com.jvcodingsolutions.smartstep.features.report

sealed interface ReportAction {
    data object OnPreviousWeekClick: ReportAction
    data object OnNextWeekClick: ReportAction
    data class OnMetricSelected(val metric: ReportMetric): ReportAction
    data object OnBackClick: ReportAction
}
