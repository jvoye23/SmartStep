package com.jvcodingsolutions.smartstep.core.di

import com.jvcodingsolutions.smartstep.core.database.DatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single { DatabaseFactory(androidContext()) }
}
