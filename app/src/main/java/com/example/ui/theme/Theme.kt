package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkPrimary,
    tertiary = SleekSuccess,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onPrimaryContainer = DarkOnPrimaryContainer,
    onSecondary = DarkOnPrimary,
    onTertiary = Color.White,
    onBackground = DarkTextLight,
    onSurface = DarkTextLight,
    surfaceVariant = Color(0xFF2E2B36),
    onSurfaceVariant = Color(0xFFCAC4D0)
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPrimary,
    primaryContainer = SleekPrimaryContainer,
    secondary = SleekPrimary,
    tertiary = SleekSuccess,
    background = SleekBackground,
    surface = SleekSurface,
    onPrimary = SleekOnPrimary,
    onPrimaryContainer = SleekOnPrimaryContainer,
    onSecondary = SleekOnPrimary,
    onTertiary = Color.White,
    onBackground = SleekTextDark,
    onSurface = SleekTextDark,
    surfaceVariant = Color(0xFFF3F4F9),
    onSurfaceVariant = SleekTextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

