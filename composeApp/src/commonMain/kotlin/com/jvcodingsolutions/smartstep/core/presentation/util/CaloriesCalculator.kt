package com.jvcodingsolutions.smartstep.core.presentation.util

import com.jvcodingsolutions.smartstep.core.domain.model.Gender
import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo
import kotlin.math.roundToInt

/**
 * Calculates the estimated kilocalories (kcal) burned based on steps and user profile.
 *
 * @param steps The total number of steps taken.
 * @param profileInfo The user's domain model containing weight and gender data.
 * @return The estimated calories burned, rounded to the nearest whole number.
 */
fun calculateCalories(steps: Int, profileInfo: ProfileInfo): Int {
    if (steps <= 0) return 0

    // 1. Determine weight in kilograms safely based on their chosen system
    val weightKg = when {
        profileInfo.isMetricSystem && profileInfo.weightInKg != null -> profileInfo.weightInKg.toDouble()
        !profileInfo.isMetricSystem && profileInfo.weightInLbs != null -> profileInfo.weightInLbs * 0.453592
        // Fallback: If the expected weight is missing, we cannot calculate calories
        else -> return 0
    }

    // 2. Apply the gender metabolic factor
    val genderFactor = when (profileInfo.gender) {
        Gender.MALE -> 1.0
        Gender.FEMALE -> 0.9
    }

    // 3. The formula
    val kcalPerStep = weightKg * 0.0005 * genderFactor
    val calories = steps * kcalPerStep

    // 4. Return as a rounded whole number
    return calories.roundToInt()
}