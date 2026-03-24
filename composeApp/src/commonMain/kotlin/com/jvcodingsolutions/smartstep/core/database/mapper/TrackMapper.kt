package com.jvcodingsolutions.smartstep.core.database.mapper

import com.jvcodingsolutions.smartstep.core.database.entity.TrackEntity
import com.jvcodingsolutions.smartstep.core.domain.model.Tracks
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.milliseconds

fun TrackEntity.toDomainModel(): Tracks {
    return Tracks(
        id = id,
        profileId = profileId,
        dailyStepGoal = dailyStepGoal,
        currentSteps = currentSteps,
        calories = calories,
        minutes = minutesMillis?.milliseconds,
        currentDate = LocalDate.fromEpochDays(currentDate)
    )
}

fun Tracks.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        profileId = profileId,
        dailyStepGoal = dailyStepGoal,
        currentSteps = currentSteps,
        calories = calories,
        minutesMillis = minutes?.inWholeMilliseconds,
        currentDate = currentDate.toEpochDays()
    )
}
