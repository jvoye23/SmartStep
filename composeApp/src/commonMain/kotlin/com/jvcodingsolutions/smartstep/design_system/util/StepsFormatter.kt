package com.jvcodingsolutions.smartstep.design_system.util

//Pure Kotlin number formatter (e.g., converts 4523 to "4,523")
fun formattedSteps(steps: Int): String {
    val formattedSteps = steps.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return formattedSteps
}