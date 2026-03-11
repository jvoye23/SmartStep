package com.jvcodingsolutions.smartstep.core.data.profile

import androidx.sqlite.SQLiteException
import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import com.jvcodingsolutions.smartstep.core.database.dao.ProfileInfoDao
import com.jvcodingsolutions.smartstep.core.database.mapper.toProfileInfo
import com.jvcodingsolutions.smartstep.core.database.mapper.toProfileInfoEntity
import com.jvcodingsolutions.smartstep.core.domain.ProfileInfoId
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.model.ProfileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class RoomLocalProfileDataSource(
    private val profileInfoDao: ProfileInfoDao
): ProfileStorage {
    override suspend fun get(): ProfileInfo? = withContext(Dispatchers.IO) {
        profileInfoDao.getProfile()?.toProfileInfo()
    }

    override suspend fun upsert(profileInfo: ProfileInfo): Result<ProfileInfoId, DataError.Local> {
        return try {
            val profileInfoEntity = profileInfo.toProfileInfoEntity()
            profileInfoDao.upsertProfile(profileInfoEntity)
            Result.Success(data = profileInfo.id)

        } catch (e: SQLiteException) {
            Result.Error(error = DataError.Local.DISK_FULL)
        }
    }
}