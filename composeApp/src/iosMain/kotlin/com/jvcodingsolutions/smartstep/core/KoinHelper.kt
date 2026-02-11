package com.jvcodingsolutions.smartstep.core

import com.jvcodingsolutions.smartstep.di.appModule
import com.jvcodingsolutions.smartstep.di.initKoin


fun startKoinIos() {
    initKoin {
        modules(
            appModule,
            //coreDataModule

        )
    }
}