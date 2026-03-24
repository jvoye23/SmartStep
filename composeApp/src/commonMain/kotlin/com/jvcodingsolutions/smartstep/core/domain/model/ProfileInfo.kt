package com.jvcodingsolutions.smartstep.core.domain.model

import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class ProfileInfo(
    val id: String,
    val gender: Gender,
    val weightInKg: Int? = null,
    val weightInLbs: Int? = null,
    val heightInCm: Int? = null,
    val feet: Int? = null,
    val inches: Int? = null,
    val isMetricSystem: Boolean
)
