package com.stockalert.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1F88E5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD9E8FF),
    onPrimaryContainer = Color(0xFF001A49),
    secondary = Color(0xFF575E6F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE1F7),
    onSecondaryContainer = Color(0xFF141B2B),
    tertiary = Color(0xFF715573),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFDD8FB),
    onTertiaryContainer = Color(0xFF29132E),
    error = Color(0xFFB3261E),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFEFF0F4),
    onSurfaceVariant = Color(0xFF49454E),
    outline = Color(0xFF7A757F),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFAFC6FF),
    onPrimary = Color(0xFF003062),
    primaryContainer = Color(0xFF004891),
    onPrimaryContainer = Color(0xFFD9E8FF),
    secondary = Color(0xFFC0C7D8),
    onSecondary = Color(0xFF2A3140),
    secondaryContainer = Color(0xFF404857),
    onSecondaryContainer = Color(0xFFDCE1F7),
    tertiary = Color(0xFFE6ACDF),
    onTertiary = Color(0xFF402244),
    tertiaryContainer = Color(0xFF57385C),
    onTertiaryContainer = Color(0xFFFDD8FB),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE7E0E6),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE7E0E6),
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = Color(0xFFCAC4CF),
    outline = Color(0xFF948F99),
)

@Composable
fun StockAlertTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
