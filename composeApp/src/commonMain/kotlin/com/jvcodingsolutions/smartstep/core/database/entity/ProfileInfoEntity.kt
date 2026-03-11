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
    val weight: Int,
    val height: Int
)