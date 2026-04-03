package com.jvcodingsolutions.smartstep.core.presentation.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Calculates the amount of "active time" to add based on the gap between two activity detections.
 * 
 * Logic:
 * - If the gap is small (<= [activeThreshold]), we consider the entire interval as active time.
 * - If the gap is large (> [activeThreshold]), we assume the user was idle and only count a 
 *   minimum active slice (e.g. 1 second) for the event that just occurred.
 */
fun calculateActiveTimeDelta(
    timeSinceLastDetection: Duration?,
    activeThreshold: Duration = 10.seconds
): Duration {
    // If it's the very first step of the session, count it as a 1-second activity burst
    if (timeSinceLastDetection == null) return 1.seconds 

    return if (timeSinceLastDetection <= activeThreshold) {
        timeSinceLastDetection
    } else {
        // User likely stopped and started again. Count the new step as 1s of activity.
        1.seconds 
    }
}

/**
 * Formats the total duration into a string like "15 min".
 *
 * Requirements:
 * - Total duration of activity for the current day.
 * - The value is always displayed in minutes (min).
 * - If activity duration is less than one minute, 0 min is shown.
 * - The value is rounded to the nearest whole minute.
 */
fun formatActivityDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    
    // "If activity duration is less than one minute, 0 min is shown."
    if (totalSeconds < 60) return "0 min"
    
    // "The value is rounded to the nearest whole minute."
    // (seconds + 30) / 60 handles the 0.5 round-up logic.
    val roundedMinutes = (totalSeconds + 30) / 60
    
    return "$roundedMinutes min"
}
