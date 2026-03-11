package com.jvcodingsolutions.smartstep.features.step_counter.di

import com.jvcodingsolutions.smartstep.features.step_counter.StepCounterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val stepCounterModule = module {
    viewModelOf(::StepCounterViewModel)
}