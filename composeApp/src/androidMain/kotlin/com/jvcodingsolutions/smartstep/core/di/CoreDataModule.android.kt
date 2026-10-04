package com.jvcodingsolutions.smartstep.core.di

import com.jvcodingsolutions.smartstep.core.data.connectivity.AndroidConnectivityObserver
import com.jvcodingsolutions.smartstep.core.data.track.AndroidStepTracker
import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import com.jvcodingsolutions.smartstep.core.domain.connectivity.ConnectivityObserver
import com.jvcodingsolutions.smartstep.core.domain.track.StepTracker
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single { DatabaseFactory(androidContext()) }
    single { AndroidStepTracker(androidContext()) } bind StepTracker::class
    single<HttpClientEngine> { OkHttp.create() }
    single { AndroidConnectivityObserver(androidContext()) } bind ConnectivityObserver::class
}
