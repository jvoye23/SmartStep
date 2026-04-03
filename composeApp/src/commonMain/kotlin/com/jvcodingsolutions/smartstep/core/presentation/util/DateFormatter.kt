package com.jvcodingsolutions.smartstep.core.presentation.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Formats a LocalDate to a string in the format "yyyy/MM/dd"
 * Example: 2025/11/30
 */
fun formattedDate(date: LocalDate): String {
    val year = date.year
    val month = date.month.number.toString().padStart(2, '0')
    val day = date.day.toString().padStart(2, '0')
    
    return "$year/$month/$day"
}
