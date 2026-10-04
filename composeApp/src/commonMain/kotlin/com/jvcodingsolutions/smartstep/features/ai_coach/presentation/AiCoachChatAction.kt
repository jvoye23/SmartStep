package com.jvcodingsolutions.smartstep.features.ai_coach.presentation

sealed interface AiCoachChatAction {
    data class OnInputChanged(val text: String): AiCoachChatAction
    data object OnSendClick: AiCoachChatAction
    data class OnSuggestionClick(val prompt: String): AiCoachChatAction
    data object OnToggleSuggestions: AiCoachChatAction
    data object OnBackClick: AiCoachChatAction
}
