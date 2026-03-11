package com.jvcodingsolutions.smartstep.di

import com.jvcodingsolutions.smartstep.core.di.coreDataModule
import com.jvcodingsolutions.smartstep.features.profile_setup.di.profileSetupModule
import com.jvcodingsolutions.smartstep.features.step_counter.di.stepCounterModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            profileSetupModule,
            stepCounterModule,
            coreDataModule
        )
    }
}