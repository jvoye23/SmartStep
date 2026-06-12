package com.jvcodingsolutions.smartstep.core.di

import com.jvcodingsolutions.smartstep.core.data.track.AndroidStepTracker
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single { DatabaseFactory(androidContext()) }
    single { AndroidStepTracker(androidContext()) } bind StepTracker::class
}
