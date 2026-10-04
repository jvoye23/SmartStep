@file:OptIn(ExperimentalMaterial3Api::class)

package com.jvcodingsolutions.smartstep.features.ai_coach.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jvcodingsolutions.smartstep.core.presentation.util.isWideScreenLayout
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundSecondary
import com.jvcodingsolutions.smartstep.design_system.theme.bodyMediumRegular
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatMessage
import com.jvcodingsolutions.smartstep.features.ai_coach.domain.ChatRole
import com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components.AiMessageBubble
import com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components.ChatInputField
import com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components.QuickSuggestions
import com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components.UserMessageBubble
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import smartstep.composeapp.generated.resources.Res
import smartstep.composeapp.generated.resources.ai_coach
import smartstep.composeapp.generated.resources.navigate_back

@Composable
fun AiCoachChatScreenRoot(
    viewModel: AiCoachChatViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AiCoachChatScreen(
        state = state,
        onAction = { action ->
            when (action) {
                AiCoachChatAction.OnBackClick -> onNavigateBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun AiCoachChatScreen(
    state: AiCoachChatState,
    onAction: (AiCoachChatAction) -> Unit
) {
    val isWideScreen = isWideScreenLayout()

    // Wide screens constrain the message history to 600dp and user bubbles to 400dp
    val messageContainerMaxWidth: Dp? = if (isWideScreen) 600.dp else null
    val userBubbleMaxWidth: Dp? = if (isWideScreen) 400.dp else null

    val listState = rememberLazyListState()
    val visibleMessages = state.visibleMessages

    LaunchedEffect(visibleMessages.size, state.isAiResponding) {
        val itemCount = visibleMessages.size + if (state.isAiResponding) 1 else 0
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.backgroundSecondary,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { onAction(AiCoachChatAction.OnBackClick) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.navigate_back),
                                tint = MaterialTheme.colorScheme.textPrimary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(Res.string.ai_coach),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.textPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.backgroundSecondary
                    )
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.strokeMain)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.backgroundSecondary)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .let { if (messageContainerMaxWidth != null) it.widthIn(max = messageContainerMaxWidth) else it },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = visibleMessages.size,
                        key = { index -> visibleMessages[index].id }
                    ) { index ->
                        val message = visibleMessages[index]
                        when (message.role) {
                            ChatRole.MODEL -> AiMessageBubble(text = message.text)
                            ChatRole.USER -> UserMessageBubble(
                                text = message.text,
                                maxBubbleWidth = userBubbleMaxWidth
                            )
                        }
                    }
                    if (state.isAiResponding) {
                        items(count = 1, key = { "ai_typing_indicator" }) {
                            AiMessageBubble(text = "", isLoading = true)
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.strokeMain)

            // On wide screens the bottom section is centered with a fixed 400dp width
            Column(
                modifier = if (isWideScreen) {
                    Modifier.width(400.dp)
                } else {
                    Modifier.fillMaxWidth()
                }.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                state.error?.let { error ->
                    Text(
                        text = error.asString(),
                        style = MaterialTheme.typography.bodyMediumRegular,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                QuickSuggestions(
                    isExpanded = state.isSuggestionsExpanded,
                    onToggleExpanded = { onAction(AiCoachChatAction.OnToggleSuggestions) },
                    onSuggestionClick = { suggestion ->
                        onAction(AiCoachChatAction.OnSuggestionClick(suggestion))
                    }
                )
                ChatInputField(
                    inputText = state.inputText,
                    isOnline = state.isOnline,
                    canSend = state.canSend,
                    onInputChanged = { onAction(AiCoachChatAction.OnInputChanged(it)) },
                    onSendClick = { onAction(AiCoachChatAction.OnSendClick) }
                )
            }
        }
    }
}

private val previewMessages = listOf(
    ChatMessage(
        id = "1",
        role = ChatRole.MODEL,
        text = "Hello! I'm your AI fitness coach. I've noticed your activity levels are a " +
                "bit lower than usual today. I'm here to help you get back on track and " +
                "answer any questions you might have about your fitness journey."
    ),
    ChatMessage(
        id = "2",
        role = ChatRole.USER,
        text = "What should I do to increase my activity today?"
    ),
    ChatMessage(
        id = "3",
        role = ChatRole.MODEL,
        text = "Hello! I'm your AI fitness coach. I've noticed your activity levels are a " +
                "bit lower than usual today. I'm here to help you get back on track and " +
                "answer any questions you might have about your fitness journey."
    ),
    ChatMessage(
        id = "4",
        role = ChatRole.USER,
        text = "What should I do to increase my activity today?"
    )
)

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AiCoachChatScreenPreview() {
    SmartStepTheme {
        AiCoachChatScreen(
            state = AiCoachChatState(
                messages = previewMessages,
                inputText = "What do you reco"
            ),
            onAction = {}
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AiCoachChatScreenSuggestionsPreview() {
    SmartStepTheme {
        AiCoachChatScreen(
            state = AiCoachChatState(
                messages = previewMessages,
                isSuggestionsExpanded = true
            ),
            onAction = {}
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AiCoachChatScreenOfflinePreview() {
    SmartStepTheme {
        AiCoachChatScreen(
            state = AiCoachChatState(
                messages = previewMessages,
                isOnline = false
            ),
            onAction = {}
        )
    }
}

@Preview(widthDp = 900, heightDp = 800)
@Composable
private fun AiCoachChatScreenWidePreview() {
    SmartStepTheme {
        AiCoachChatScreen(
            state = AiCoachChatState(messages = previewMessages),
            onAction = {}
        )
    }
}
