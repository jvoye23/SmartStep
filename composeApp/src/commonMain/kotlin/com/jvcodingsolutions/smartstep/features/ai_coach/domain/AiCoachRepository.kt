package com.jvcodingsolutions.smartstep.features.ai_coach.domain

import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result

interface AiCoachRepository {

    suspend fun generateInsight(
        context: ActivityContext
    ): Result<String, DataError.Network>

    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        context: ActivityContext
    ): Result<String, DataError.Network>
}
