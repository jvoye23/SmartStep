package com.jvcodingsolutions.smartstep.core.domain

import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo

typealias ProfileInfoId = String

interface ProfileStorage {
    suspend fun get(): ProfileInfo?
    suspend fun upsert(profileInfo: ProfileInfo): Result<ProfileInfoId, DataError.Local>
}