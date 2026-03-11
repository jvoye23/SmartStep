package com.jvcodingsolutions.smartstep.di

import com.jvcodingsolutions.smartstep.app.MainViewModel
import com.jvcodingsolutions.smartstep.core.data.profile.RoomLocalProfileDataSource
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {


    single(named("AppScope")) {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    viewModelOf(::MainViewModel)
}