package com.jvcodingsolutions.smartstep.core.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SmartStepDatabaseConstructor: RoomDatabaseConstructor<SmartStepDatabase> {
    override fun initialize(): SmartStepDatabase
}