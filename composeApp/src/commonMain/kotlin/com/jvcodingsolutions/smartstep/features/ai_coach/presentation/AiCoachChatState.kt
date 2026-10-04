package com.jvcodingsolutions.smartstep.features.ai_coach.presentation

import com.jvcodingsolutions.smartstep.core.presentation.util.UiText
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatMessage

data class AiCoachChatState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isAiResponding: Boolean = false,
    val isOnline: Boolean = true,
    val isSuggestionsExpanded: Boolean = false,
    val error: UiText? = null
) {
    val visibleMessages: List<ChatMessage>
        get() = messages.filter { it.isVisible }

    val canSend: Boolean
        get() = inputText.trim().isNotEmpty() && isOnline && !isAiResponding
}
