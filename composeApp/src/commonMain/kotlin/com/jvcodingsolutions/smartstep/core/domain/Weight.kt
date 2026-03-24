package com.jvcodingsolutions.smartstep.core.domain

data class Weight(
    val kg: Int? = null,
    val lbs: Int? = null
) {
    fun toLbs(): Int {
        return lbs ?: kg?.times(2.20462)?.toInt() ?: 999
    }
    fun toKg(): Int {
        return kg ?: lbs?.div(2.20462)?.toInt() ?: 999
    }
}

fun Int.toLbs(): Int = this * 2.20462.toInt()


fun Int.toKg(): Int = this / 2.20462.toInt()
