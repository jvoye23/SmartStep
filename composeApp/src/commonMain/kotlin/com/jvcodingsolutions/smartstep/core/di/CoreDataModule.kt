package com.jvcodingsolutions.smartstep.core.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.jvcodingsolutions.smartstep.core.data.profile.RoomLocalProfileDataSource
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.core.database.SmartStepDatabase
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)

    single { get<SmartStepDatabase>().profileInfoDao }

    singleOf(::RoomLocalProfileDataSource) bind ProfileStorage::class

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

}