package com.jvcodingsolutions.smartstep.features.ai_coach.di

import com.jvcodingsolutions.smartstep.features.ai_coach.data.AiCoachRepositoryImpl
import com.jvcodingsolutions.smartstep.features.ai_coach.data.GeminiRemoteDataSource
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.AiCoachRepository
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.InsightSessionHolder
import com.jvcodingsolutions.smartstep.features.ai_coach.presentation.AiCoachChatViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val aiCoachModule = module {
    singleOf(::GeminiRemoteDataSource)
    singleOf(::AiCoachRepositoryImpl) bind AiCoachRepository::class
    single { InsightSessionHolder() }
    viewModelOf(::AiCoachChatViewModel)
}
