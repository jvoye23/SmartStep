package com.jvcodingsolutions.smartstep.core.presentation.util

import kotlin.math.round

enum class MeasurementSystem {
    METRIC,
    IMPERIAL
}

/**
 * Calculates the total distance covered based on steps and user height.
 * * @param steps The total number of steps taken.
 * @param heightCm The user's height strictly in centimeters.
 * @param system The user's preferred measurement system (Metric or Imperial).
 * @return A formatted string rounded to one decimal place (e.g., "1.2", "0.8").
 */
fun calculateFormattedDistance(
    steps: Int,
    heightCm: Int,
    system: MeasurementSystem
): String {
    return "${calculateDistance(steps, heightCm, system)}"
}

/**
 * Numeric variant of [calculateFormattedDistance], rounded to one decimal place.
 * Used where distances need to be summed (e.g. weekly report) before display.
 */
fun calculateDistance(
    steps: Int,
    heightCm: Int,
    system: MeasurementSystem
): Double {
    if (steps <= 0) {
        return 0.0
    }

    // 1. Step length approximation
    val stepLengthCm = heightCm * 0.415

    // 2. Base formula: Distance in meters
    val distanceMeters = (steps * stepLengthCm) / 100.0

    // 3. Unit conversion
    val convertedDistance = when (system) {
        MeasurementSystem.METRIC -> distanceMeters / 1000.0
        MeasurementSystem.IMPERIAL -> distanceMeters / 1609.3
    }

    // 4. KMP-Safe Rounding to 1 decimal place (e.g., 1.254 -> 12.54 -> 13.0 -> 1.3)
    return round(convertedDistance * 10.0) / 10.0
}

/**
 * Formats an already-computed distance value to one decimal place.
 */
fun formatDistanceValue(distance: Double): String {
    return "${round(distance * 10.0) / 10.0}"
}