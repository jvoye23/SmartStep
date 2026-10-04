package com.jvcodingsolutions.smartstep.core.di

import com.jvcodingsolutions.smartstep.core.data.connectivity.IosConnectivityObserver
import com.jvcodingsolutions.smartstep.core.data.track.IOSStepTracker
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.core.domain.connectivity.ConnectivityObserver
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single { DatabaseFactory() }
    single<HttpClientEngine> { Darwin.create() }
    single { IosConnectivityObserver() } bind ConnectivityObserver::class
    single {
        IOSStepTracker(
            // Lazy to break the DI cycle: TrackRepositoryImpl also depends on StepTracker
            lazyTrackRepository = lazy { get<TrackRepository>() },
            profileStorage = get()
        )
    } bind StepTracker::class
}
