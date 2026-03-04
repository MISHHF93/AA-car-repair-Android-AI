package com.aa.carrepair.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AANavy = Color(0xFF003087)
private val AAAmber = Color(0xFFFFD700)
private val SafetyCritical = Color(0xFFD32F2F)
private val SafetyHigh = Color(0xFFF57C00)
private val SafetyMedium = Color(0xFFFBC02D)
private val SafetyLow = Color(0xFF388E3C)

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
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = SafetyCritical,
    onError = Color.White
)

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
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun AACarRepairTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AATypography,
        content = content
    )
}
