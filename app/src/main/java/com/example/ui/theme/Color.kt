package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeManager {
    var isLightMode by mutableStateOf(false)
}

val PrimaryEmerald = Color(0xFF00C853)
val PrimaryEmeraldDark = Color(0xFF009624)
val AccentGold = Color(0xFFFFC107)
val AccentGoldLight = Color(0xFFFFE082)
val AccentBlue = Color(0xFF0288D1)

private val ActualDarkBackground = Color(0xFF0A111E)
private val ActualDarkSurface = Color(0xFF142032)
private val ActualDarkCardSurface = Color(0xFF1E2D44)
private val ActualDarkCardBorder = Color(0xFF2A3E5C)

val LightBackground = Color(0xFFF1F5F9)
val LightSurface = Color(0xFFFFFFFF)
val LightCardSurface = Color(0xFFE2E8F0)
val LightCardBorder = Color(0xFFCBD5E1)

private val ActualTextPrimary = Color(0xFFF0F4F8)
private val ActualTextSecondary = Color(0xFF94A3B8)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)

val SuccessGreen = Color(0xFF00E676)
val ErrorRed = Color(0xFFFF5252)

val DarkBackground: Color
    get() = if (ThemeManager.isLightMode) LightBackground else ActualDarkBackground

val DarkSurface: Color
    get() = if (ThemeManager.isLightMode) LightSurface else ActualDarkSurface

val DarkCardSurface: Color
    get() = if (ThemeManager.isLightMode) LightCardSurface else ActualDarkCardSurface

val DarkCardBorder: Color
    get() = if (ThemeManager.isLightMode) LightCardBorder else ActualDarkCardBorder

val TextPrimary: Color
    get() = if (ThemeManager.isLightMode) LightTextPrimary else ActualTextPrimary

val TextSecondary: Color
    get() = if (ThemeManager.isLightMode) LightTextSecondary else ActualTextSecondary

