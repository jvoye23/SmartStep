package com.jvcodingsolutions.smartstep.core.domain.model

import com.jvcodingsolutions.smartstep.core.domain.Height
import com.jvcodingsolutions.smartstep.core.domain.Weight
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ProfileInfo(
    val id: String,
    val gender: Gender,
    val weight: Int,
    val height: Int
)
