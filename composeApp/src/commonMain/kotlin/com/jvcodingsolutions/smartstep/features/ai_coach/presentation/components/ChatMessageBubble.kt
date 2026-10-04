package com.jvcodingsolutions.smartstep.features.ai_coach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jvcodingsolutions.smartstep.design_system.theme.Icon_AI
import com.jvcodingsolutions.smartstep.design_system.theme.SmartStepTheme
import com.jvcodingsolutions.smartstep.design_system.theme.backgroundWhite
import com.jvcodingsolutions.smartstep.design_system.theme.bodyLargeRegular
import com.jvcodingsolutions.smartstep.design_system.theme.buttonPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.strokeMain
import com.jvcodingsolutions.smartstep.design_system.theme.textPrimary
import com.jvcodingsolutions.smartstep.design_system.theme.textWhite

private val AiBubbleShape = RoundedCornerShape(
    topStart = 4.dp,
    topEnd = 16.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

private val UserBubbleShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 4.dp,
    bottomStart = 16.dp,
    bottomEnd = 16.dp
)

@Composable
fun AiMessageBubble(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.buttonPrimary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icon_AI,
                contentDescription = "AI Coach",
                tint = MaterialTheme.colorScheme.textWhite,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.backgroundWhite,
                    shape = AiBubbleShape
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.strokeMain,
                    shape = AiBubbleShape
                )
                .padding(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.buttonPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLargeRegular,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }
        }
    }
}

@Composable
fun UserMessageBubble(
    modifier: Modifier = Modifier,
    text: String,
    // null = mobile behavior (max 75% of the available width); fixed dp on wide screens
    maxBubbleWidth: Dp? = null
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val resolvedMaxWidth = maxBubbleWidth ?: (maxWidth * 0.75f)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .widthIn(max = resolvedMaxWidth)
                .background(
                    color = MaterialTheme.colorScheme.buttonPrimary,
                    shape = UserBubbleShape
                )
                .padding(12.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLargeRegular,
                color = MaterialTheme.colorScheme.textWhite
            )
        }
    }
}

@Preview
@Composable
private fun ChatMessageBubblesPreview() {
    SmartStepTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AiMessageBubble(
                text = "Hello! I'm your AI fitness coach. I've noticed your activity levels " +
                        "are a bit lower than usual today. I'm here to help you get back on " +
                        "track and answer any questions you might have about your fitness journey."
            )
            Spacer(modifier = Modifier.height(16.dp))
            UserMessageBubble(
                text = "What should I do to increase my activity today?"
            )
            Spacer(modifier = Modifier.height(16.dp))
            AiMessageBubble(text = "", isLoading = true)
        }
    }
}
