package com.jvcodingsolutions.smartstep.features.ai_coach.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jvcodingsolutions.multipizza.core.domain.util.Result
import com.jvcodingsolutions.smartstep.core.domain.ProfileStorage
import com.jvcodingsolutions.smartstep.core.domain.connectivity.ConnectivityObserver
import com.jvcodingsolutions.smartstep.core.domain.repository.TrackRepository
import com.jvcodingsolutions.smartstep.core.presentation.util.asUiText
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ActivityContext
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.AiCoachRepository
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.AiPrompts
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatMessage
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class AiCoachChatViewModel(
    private val aiCoachRepository: AiCoachRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val trackRepository: TrackRepository,
    private val profileStorage: ProfileStorage
): ViewModel() {

    private val _state = MutableStateFlow(AiCoachChatState())

    private var hasLoadedInitialData = false
    private var hasSentStarterMessage = false
    private var activityContext: ActivityContext? = null

    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                observeConnectivity()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            _state.value
        )

    fun onAction(action: AiCoachChatAction) {
        when(action) {
            is AiCoachChatAction.OnInputChanged -> {
                _state.update { it.copy(inputText = action.text) }
            }
            AiCoachChatAction.OnSendClick -> {
                val text = _state.value.inputText.trim()
                if (_state.value.canSend) {
                    _state.update { it.copy(inputText = "") }
                    sendMessage(text)
                }
            }
            is AiCoachChatAction.OnSuggestionClick -> {
                if (_state.value.isOnline && !_state.value.isAiResponding) {
                    _state.update { it.copy(isSuggestionsExpanded = false) }
                    sendMessage(action.prompt)
                }
            }
            AiCoachChatAction.OnToggleSuggestions -> {
                _state.update { it.copy(isSuggestionsExpanded = !it.isSuggestionsExpanded) }
            }
            AiCoachChatAction.OnBackClick -> Unit // handled by the navigation layer
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.isConnected.collect { isOnline ->
                _state.update { it.copy(isOnline = isOnline) }
                // The session opens with an automatic AI starter message; if the screen
                // was opened offline, it is sent as soon as the connection is available
                if (isOnline && !hasSentStarterMessage) {
                    hasSentStarterMessage = true
                    sendStarterMessage()
                }
            }
        }
    }

    private suspend fun buildActivityContext(): ActivityContext {
        activityContext?.let { return it }

        val profileInfo = profileStorage.get()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val steps = profileInfo?.let {
            trackRepository.getLiveStepsFlow(it.id, today).first()
        } ?: 0
        val goal = profileInfo?.let {
            trackRepository.getCurrentStepGoal(it.id, today)
        } ?: 6000
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour

        return ActivityContext.create(
            currentSteps = steps,
            dailyStepGoal = goal,
            hourOfDay = hour
        ).also { activityContext = it }
    }

    private fun sendStarterMessage() {
        viewModelScope.launch {
            val starter = ChatMessage(
                role = ChatRole.USER,
                text = AiPrompts.STARTER_PROMPT,
                isVisible = false
            )
            _state.update { it.copy(messages = it.messages + starter) }
            requestAiResponse()
        }
    }

    private fun sendMessage(text: String) {
        viewModelScope.launch {
            val message = ChatMessage(role = ChatRole.USER, text = text)
            _state.update { it.copy(messages = it.messages + message, error = null) }
            requestAiResponse()
        }
    }

    private suspend fun requestAiResponse() {
        _state.update { it.copy(isAiResponding = true) }
        val context = buildActivityContext()
        when (val result = aiCoachRepository.sendChatMessage(_state.value.messages, context)) {
            is Result.Success -> {
                val reply = ChatMessage(role = ChatRole.MODEL, text = result.data)
                _state.update { it.copy(
                    messages = it.messages + reply,
                    isAiResponding = false
                ) }
            }
            is Result.Error -> {
                _state.update { it.copy(
                    isAiResponding = false,
                    error = result.error.asUiText()
                ) }
            }
        }
    }
}
