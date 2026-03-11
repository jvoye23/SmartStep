package com.jvcodingsolutions.smartstep.core.domain

data class Weight(
    val kg: Int? = null,
    val lbs: Int? = null
) {
    fun toLbs(): Int {
        return kg?.times(2.20462.toInt()) ?: 999
    }

    fun toKg(): Int {
        return lbs?.div(2.20462)?.toInt() ?: 999
    }
}
