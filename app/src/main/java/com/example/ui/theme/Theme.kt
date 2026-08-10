package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color.Black,
    primaryContainer = PrimaryEmeraldDark,
    secondary = AccentGold,
    onSecondary = Color.Black,
    tertiary = AccentBlue,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color.White,
    primaryContainer = PrimaryEmerald,
    secondary = AccentGold,
    onSecondary = Color.Black,
    tertiary = AccentBlue,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCardSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder,
    error = ErrorRed
)

@Composable
fun InvestProTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

