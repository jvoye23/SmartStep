package com.jvcodingsolutions.smartstep.features.ai_coach.domain

import com.benasher44.uuid.uuid4

data class ChatMessage(
    val id: String = uuid4().toString(),
    val role: ChatRole,
    val text: String,
    // The starter prompt is sent as a user message but never rendered in the UI
    val isVisible: Boolean = true
)

enum class ChatRole {
    USER,
    MODEL
}
