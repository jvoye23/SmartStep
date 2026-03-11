package com.jvcodingsolutions.smartstep.features.profile_setup.di

import com.jvcodingsolutions.smartstep.features.profile_setup.ProfileSetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileSetupModule = module {
    viewModelOf(::ProfileSetupViewModel)
}