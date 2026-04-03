package com.jvcodingsolutions.smartstep.features.step_counter.di

import com.jvcodingsolutions.smartstep.features.step_counter.StepCounterViewModel
import com.jvcodingsolutions.smartstep.navigation.SmartStepNavigationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val stepCounterModule = module {
    viewModel {
        StepCounterViewModel(
            trackRepository = get(),
            profileStorage = get(),
            stepTracker = get(),
            applicationScope = get(named("AppScope"))
        )
    }
    viewModelOf(::SmartStepNavigationViewModel)
}