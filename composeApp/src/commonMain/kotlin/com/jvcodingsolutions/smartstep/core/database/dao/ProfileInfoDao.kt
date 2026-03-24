package com.jvcodingsolutions.smartstep.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.jvcodingsolutions.smartstep.core.database.entity.ProfileInfoEntity

@Dao
interface ProfileInfoDao {

    @Upsert
    suspend fun upsertProfile(profileInfo: ProfileInfoEntity)

    @Query("SELECT * FROM profile")
    suspend fun getProfile(): ProfileInfoEntity?
}