@file:OptIn(ExperimentalUuidApi::class)

package com.jvcodingsolutions.smartstep.core.database.mapper

import com.jvcodingsolutions.smartstep.core.database.entity.ProfileInfoEntity
import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun ProfileInfoEntity.toProfileInfo(): ProfileInfo {
    return ProfileInfo(
        id = id,
        gender = gender,
        weight = weight,
        height = height
    )
}

fun ProfileInfo.toProfileInfoEntity(): ProfileInfoEntity {
    return ProfileInfoEntity(
        id = id,
        gender = gender,
        weight = weight,
        height = height
    )
}