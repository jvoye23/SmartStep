package com.jvcodingsolutions.smartstep.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jvcodingsolutions.smartstep.core.domain.model.Gender
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Entity(tableName = "profile")
data class ProfileInfoEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val gender: Gender,
    val weightInKg: Int? = null,
    val weightInLbs: Int? = null,
    val heightInCm: Int? = null,
    val feet: Int? = null,
    val inches: Int? = null,
    val isMetricSystem: Boolean
)