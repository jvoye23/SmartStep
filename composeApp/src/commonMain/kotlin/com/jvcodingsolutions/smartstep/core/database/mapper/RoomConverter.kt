package com.jvcodingsolutions.smartstep.core.database.mapper

import androidx.room.TypeConverter
import com.jvcodingsolutions.smartstep.core.domain.model.Gender
import kotlinx.serialization.json.Json

class RoomConverters {

    private val jsonHandler = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromStringToGender(value: String): Gender {
        return try {
            Gender.valueOf(value)
        } catch (e: IllegalArgumentException) {
            // Fallback strategy: If the DB contains a string not found in the Enum
            // (e.g., due to an app update removing a category), return a default.
            Gender.MALE
        }
    }

    @TypeConverter
    fun fromGenderToString(gender: Gender): String {
        return gender.name
    }
}