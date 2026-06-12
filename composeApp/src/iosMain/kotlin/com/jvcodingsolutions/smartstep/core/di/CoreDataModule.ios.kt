package com.jvcodingsolutions.smartstep.core.di

import com.jvcodingsolutions.smartstep.core.data.track.IOSStepTracker
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single { DatabaseFactory() }
    single {
        IOSStepTracker(
            // Lazy to break the DI cycle: TrackRepositoryImpl also depends on StepTracker
            lazyTrackRepository = lazy { get<TrackRepository>() },
            profileStorage = get()
        )
    } bind StepTracker::class
}
