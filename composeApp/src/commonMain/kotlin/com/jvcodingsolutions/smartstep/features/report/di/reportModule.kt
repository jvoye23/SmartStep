package com.jvcodingsolutions.smartstep.features.report.di

import com.jvcodingsolutions.smartstep.features.report.ReportViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val reportModule = module {
    viewModelOf(::ReportViewModel)
}
