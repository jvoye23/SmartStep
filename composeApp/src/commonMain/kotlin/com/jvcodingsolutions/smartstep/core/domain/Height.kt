package com.jvcodingsolutions.smartstep.core.domain

data class Height(
    val cm: Int? = null,
    val feet: Int? = null,
    val inches: Int? = null
) {
    fun toCm(): Int {
        return cm ?: ((feet ?: 0) * 30.48 + (inches ?: 0) * 2.54).toInt()
    }

    fun toFeetInches(): Pair<Int, Int> {
        val totalCm = cm ?: ((feet ?: 0) * 30.48 + (inches ?: 0) * 2.54).toInt()
        val totalInches = (totalCm / 2.54).toInt()
        return Pair(totalInches / 12, totalInches % 12)
    }
}