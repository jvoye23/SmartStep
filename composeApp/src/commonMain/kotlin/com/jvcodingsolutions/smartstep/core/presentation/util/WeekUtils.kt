package com.jvcodingsolutions.smartstep.core.presentation.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number

/**
 * Returns the Monday of the fixed calendar week this date belongs to.
 */
fun LocalDate.startOfWeek(): LocalDate {
    return minus(dayOfWeek.isoDayNumber - 1, DateTimeUnit.DAY)
}

private val monthAbbreviations = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

/**
 * Formats a week range like "Nov 16 – Nov 22".
 */
fun formatWeekRange(start: LocalDate, end: LocalDate): String {
    val startLabel = "${monthAbbreviations[start.month.number - 1]} ${start.day}"
    val endLabel = "${monthAbbreviations[end.month.number - 1]} ${end.day}"
    return "$startLabel – $endLabel"
}

/**
 * Full day name of the week, e.g. "Monday".
 */
fun LocalDate.fullDayName(): String {
    return dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
}
