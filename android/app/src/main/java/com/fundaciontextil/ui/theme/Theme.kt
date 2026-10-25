package com.fundaciontextil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Turquesa,
    onPrimary = Negro,
    primaryContainer = Color(0xFFB4F0F2),
    onPrimaryContainer = Negro,
    secondary = Amarillo,
    onSecondary = Negro,
    secondaryContainer = Color(0xFFFFF199),
    onSecondaryContainer = Negro,
    tertiary = Negro,
    onTertiary = Blanco,
    background = Blanco,
    onBackground = Negro,
    surface = Blanco,
    onSurface = Negro,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = Negro,
    surfaceContainerLowest = Blanco,
    surfaceContainerLow = GrisClaro,
    surfaceContainer = GrisClaro,
    surfaceContainerHigh = Color(0xFFEDEDED),
    surfaceContainerHighest = Color(0xFFE6E6E6),
    outline = Color(0xFF6B6B6B),
    outlineVariant = Color(0xFFCFCFCF),
)

private val DarkColorScheme = darkColorScheme(
    primary = Turquesa,
    onPrimary = Negro,
    primaryContainer = Color(0xFF036C6E),
    onPrimaryContainer = Blanco,
    secondary = Amarillo,
    onSecondary = Negro,
    secondaryContainer = Color(0xFF665800),
    onSecondaryContainer = Blanco,
    tertiary = Blanco,
    onTertiary = Negro,
    background = Negro,
    onBackground = Blanco,
    surface = Negro,
    onSurface = Blanco,
    surfaceVariant = GrisOscuro,
    onSurfaceVariant = Blanco,
    surfaceContainerLowest = Negro,
    surfaceContainerLow = Color(0xFF141414),
    surfaceContainer = GrisOscuro,
    surfaceContainerHigh = Color(0xFF262626),
    surfaceContainerHighest = Color(0xFF303030),
    outline = Color(0xFF9A9A9A),
    outlineVariant = Color(0xFF444444),
)

@Composable
fun FundacionTextilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}