package com.jvcodingsolutions.smartstep.core.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.jvcodingsolutions.smartstep.core.data.networking.HttpClientFactory
import com.jvcodingsolutions.smartstep.core.data.profile.RoomLocalProfileDataSource
import com.jvcodingsolutions.smartstep.core.data.track.TrackRepositoryImpl
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.core.database.SmartStepDatabase
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)

    single { get<SmartStepDatabase>().profileInfoDao }
    single { get<SmartStepDatabase>().trackDao }

    singleOf(::RoomLocalProfileDataSource) bind ProfileStorage::class
    single {
        TrackRepositoryImpl(
            trackDao = get(),
            profileStorage = get(),
            stepTracker = get(),
            repositoryScope = get(named("AppScope"))
        )
    } bind TrackRepository::class

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single { HttpClientFactory.create(engine = get(), json = get()) }
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

}