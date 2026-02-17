package com.jvcodingsolutions.smartstep.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val lightScheme = lightColorScheme(
    onSecondaryContainer = OnSecondaryContainer,
)

val ColorScheme.buttonPrimary: Color
    get() = ButtonPrimary

val ColorScheme.buttonSecondary: Color
    get() = ButtonSecondary

val ColorScheme.textPrimary: Color
    get() = TextPrimary

val ColorScheme.textSecondary: Color
    get() = TextSecondary

val ColorScheme.textWhite: Color
    get() = TextWhite

val ColorScheme.backgroundMain: Color
    get() = BackgroundMain

val ColorScheme.backgroundSecondary: Color
    get() = BackgroundSecondary

val ColorScheme.backgroundTertiary: Color
    get() = BackgroundTertiary

val ColorScheme.backgroundWhite: Color
    get() = BackgroundWhite

val ColorScheme.strokeMain: Color
    get() = StrokeMain



@Composable
fun SmartStepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colorScheme = when {
        darkTheme -> lightScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}