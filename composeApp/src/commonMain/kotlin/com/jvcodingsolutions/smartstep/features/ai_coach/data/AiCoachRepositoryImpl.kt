package com.jvcodingsolutions.smartstep.features.ai_coach.data

import com.jvcodingsolutions.multipizza.core.domain.util.DataError
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiContent
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiGenerationConfig
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiPart
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiThinkingConfig
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiRequest
import com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.firstText
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ActivityContext
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.AiCoachRepository
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.AiPrompts
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatMessage
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatRole

class AiCoachRepositoryImpl(
    private val remoteDataSource: GeminiRemoteDataSource
) : AiCoachRepository {

    override suspend fun generateInsight(
        context: ActivityContext
    ): Result<String, DataError.Network> {
        val request = GeminiRequest(
            systemInstruction = systemContent(AiPrompts.INSIGHT_SYSTEM_PROMPT),
            contents = listOf(
                GeminiContent(
                    role = ROLE_USER,
                    parts = listOf(GeminiPart(AiPrompts.insightUserPrompt(context)))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.8,
                maxOutputTokens = 150,
                thinkingConfig = GeminiThinkingConfig(thinkingBudget = 0)
            )
        )
        return remoteDataSource.generateContent(request).extractText()
    }

    override suspend fun sendChatMessage(
        history: List<ChatMessage>,
        context: ActivityContext
    ): Result<String, DataError.Network> {
        val request = GeminiRequest(
            systemInstruction = systemContent(AiPrompts.chatSystemPrompt(context)),
            contents = history.map { message ->
                GeminiContent(
                    role = when (message.role) {
                        ChatRole.USER -> ROLE_USER
                        ChatRole.MODEL -> ROLE_MODEL
                    },
                    parts = listOf(GeminiPart(message.text))
                )
            },
            generationConfig = GeminiGenerationConfig(
                temperature = 0.8,
                maxOutputTokens = 600,
                thinkingConfig = GeminiThinkingConfig(thinkingBudget = 0)
            )
        )
        return remoteDataSource.generateContent(request).extractText()
    }

    private fun systemContent(prompt: String) = GeminiContent(parts = listOf(GeminiPart(prompt)))

    private fun Result<com.jvcodingsolutions.smartstep.features.ai_coach.data.dto.GeminiResponse, DataError.Network>.extractText():
            Result<String, DataError.Network> {
        return when (this) {
            is Result.Error -> Result.Error(error)
            is Result.Success -> {
                val text = data.firstText()
                if (text != null) {
                    Result.Success(text)
                } else {
                    Result.Error(DataError.Network.UNKNOWN)
                }
            }
        }
    }

    companion object {
        private const val ROLE_USER = "user"
        private const val ROLE_MODEL = "model"
    }
}
