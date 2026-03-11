package com.jvcodingsolutions.smartstep.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jvcodingsolutions.smartstep.core.database.dao.ProfileInfoDao
import com.jvcodingsolutions.smartstep.core.database.entity.ProfileInfoEntity
import com.jvcodingsolutions.smartstep.core.database.mapper.RoomConverters


@Database(
    entities = [ProfileInfoEntity::class],
    version = 1
)
@TypeConverters(RoomConverters::class)
@ConstructedBy(SmartStepDatabaseConstructor::class)
abstract class SmartStepDatabase: RoomDatabase() {
    abstract val profileInfoDao: ProfileInfoDao

    companion object {
        const val DB_NAME = "smartStep.db"
    }
}