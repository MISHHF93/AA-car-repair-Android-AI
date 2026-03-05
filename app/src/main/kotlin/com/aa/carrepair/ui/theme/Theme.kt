package com.aa.carrepair.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// ── Chat surface tones ─────────────────────────────────────────────────────────
/** Slightly warm off-white for the chat list background in light mode. */
val ChatSurfaceLight = Color(0xFFF4F6FB)
/** Deep navy-tinted dark surface for the chat list background in dark mode. */
val ChatSurfaceDark = Color(0xFF131A2E)

// ── Light scheme ───────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = AANavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E2FF),
    onPrimaryContainer = AANavy,
    secondary = AAAmber,
    onSecondary = Color(0xFF1C1B1F),
    secondaryContainer = Color(0xFFFFF3B0),
    onSecondaryContainer = Color(0xFF1C1B1F),
    tertiary = SafetyLow,
    onTertiary = Color.White,
    background = Color(0xFFF4F6FB),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE4E8F4),
    onSurfaceVariant = Color(0xFF49454F),
    surfaceContainer = Color(0xFFEEF1F8),
    surfaceContainerHigh = Color(0xFFE5E9F5),
    error = SafetyCritical,
    onError = Color.White
)

// ── Dark scheme ────────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB0C4FF),
    onPrimary = Color(0xFF002681),
    primaryContainer = Color(0xFF00399E),
    onPrimaryContainer = Color(0xFFD8E2FF),
    secondary = AAAmber,
    onSecondary = Color(0xFF3A3000),
    secondaryContainer = Color(0xFF544600),
    onSecondaryContainer = Color(0xFFFFF3B0),
    tertiary = Color(0xFF9ED882),
    onTertiary = Color(0xFF0B3900),
    background = Color(0xFF131A2E),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C2340),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2A3358),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceContainer = Color(0xFF1F2844),
    surfaceContainerHigh = Color(0xFF243050),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun AACarRepairTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AATypography,
            content = content
        )
    }
}
