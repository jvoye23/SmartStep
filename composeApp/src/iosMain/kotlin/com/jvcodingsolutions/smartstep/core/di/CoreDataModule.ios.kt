package com.jvcodingsolutions.smartstep.core.di

import com.jvcodingsolutions.smartstep.core.data.track.IOSStepTracker
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.features.step_counter.domain.StepTracker
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single { DatabaseFactory() }
    singleOf(::IOSStepTracker) bind StepTracker::class
}
