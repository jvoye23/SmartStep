package com.jvcodingsolutions.smartstep.core.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<SmartStepDatabase>
}