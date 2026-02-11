package com.jvcodingsolutions.androidapp.app

import android.app.Application
import com.jvcodingsolutions.smartstep.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class SmartStepApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SmartStepApplication)
            androidLogger()
        }
    }
}