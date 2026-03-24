package com.jvcodingsolutions.smartstep.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity = ProfileInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["profileId", "currentDate"], unique = true)
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: String,
    val dailyStepGoal: Int,
    val currentSteps: Int = 0,
    val calories: Int? = null,
    val minutesMillis: Long? = null,
    val currentDate: Long // Epoch days
)
