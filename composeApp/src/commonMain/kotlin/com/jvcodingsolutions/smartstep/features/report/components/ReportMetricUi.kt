package com.jvcodingsolutions.smartstep.features.report.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Clock
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_PinLocation
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_Sneakers
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_WeightScale
import com.jvcodingsolutions.smartstep.features.report.ReportMetric
import org.jetbrains.compose.resources.stringResource
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.calories
import smartstep.composeapp.generated.resources.distance
import smartstep.composeapp.generated.resources.kilometers
import smartstep.composeapp.generated.resources.miles
import smartstep.composeapp.generated.resources.minutes
import smartstep.composeapp.generated.resources.steps
import smartstep.composeapp.generated.resources.time
import smartstep.composeapp.generated.resources.unit_calories
import smartstep.composeapp.generated.resources.unit_kcal
import smartstep.composeapp.generated.resources.unit_kilometers
import smartstep.composeapp.generated.resources.unit_km
import smartstep.composeapp.generated.resources.unit_mi
import smartstep.composeapp.generated.resources.unit_miles
import smartstep.composeapp.generated.resources.unit_min
import smartstep.composeapp.generated.resources.unit_minutes
import smartstep.composeapp.generated.resources.unit_steps

/** Title shown in the Top Summary Card: Steps / Calories / Minutes / Kilometers */
@Composable
fun ReportMetric.summaryTitle(isMetricSystem: Boolean): String = when (this) {
    ReportMetric.STEPS -> stringResource(Res.string.steps)
    ReportMetric.CALORIES -> stringResource(Res.string.calories)
    ReportMetric.TIME -> stringResource(Res.string.minutes)
    ReportMetric.DISTANCE -> if (isMetricSystem) {
        stringResource(Res.string.kilometers)
    } else {
        stringResource(Res.string.miles)
    }
}

/** Short unit used in the Daily average label: steps / kcal / min / km */
@Composable
fun ReportMetric.averageUnit(isMetricSystem: Boolean): String = when (this) {
    ReportMetric.STEPS -> stringResource(Res.string.unit_steps)
    ReportMetric.CALORIES -> stringResource(Res.string.unit_kcal)
    ReportMetric.TIME -> stringResource(Res.string.unit_min)
    ReportMetric.DISTANCE -> if (isMetricSystem) {
        stringResource(Res.string.unit_km)
    } else {
        stringResource(Res.string.unit_mi)
    }
}

/** Unit shown next to a day item value: steps / calories / minutes / kilometers */
@Composable
fun ReportMetric.dayUnit(isMetricSystem: Boolean): String = when (this) {
    ReportMetric.STEPS -> stringResource(Res.string.unit_steps)
    ReportMetric.CALORIES -> stringResource(Res.string.unit_calories)
    ReportMetric.TIME -> stringResource(Res.string.unit_minutes)
    ReportMetric.DISTANCE -> if (isMetricSystem) {
        stringResource(Res.string.unit_kilometers)
    } else {
        stringResource(Res.string.unit_miles)
    }
}

/** Label shown in the bottom metric tab bar: Steps / Calories / Time / Distance */
@Composable
fun ReportMetric.tabLabel(): String = when (this) {
    ReportMetric.STEPS -> stringResource(Res.string.steps)
    ReportMetric.CALORIES -> stringResource(Res.string.calories)
    ReportMetric.TIME -> stringResource(Res.string.time)
    ReportMetric.DISTANCE -> stringResource(Res.string.distance)
}

@Composable
fun ReportMetric.tabIcon(): ImageVector = when (this) {
    ReportMetric.STEPS -> Icon_Sneakers
    ReportMetric.CALORIES -> Icon_WeightScale
    ReportMetric.TIME -> Icon_Clock
    ReportMetric.DISTANCE -> Icon_PinLocation
}
