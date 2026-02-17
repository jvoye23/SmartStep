package com.jvcodingsolutions.smartstep.di

import com.jvcodingsolutions.smartstep.app.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::MainViewModel)


    single(named("AppScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

}