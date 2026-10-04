package com.jvcodingsolutions.smartstep.features.ai_coach.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@Serializable
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Double? = null,
    val maxOutputTokens: Int? = null,
    val thinkingConfig: GeminiThinkingConfig? = null
)

@Serializable
data class GeminiThinkingConfig(
    // 0 disables the model's internal reasoning so the whole token budget goes to the
    // visible reply — the Gemini 3.x "flash" models otherwise truncate short answers.
    val thinkingBudget: Int
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList()
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)

fun GeminiResponse.firstText(): String? = candidates
    .firstOrNull()
    ?.content
    ?.parts
    ?.firstOrNull()
    ?.text
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
