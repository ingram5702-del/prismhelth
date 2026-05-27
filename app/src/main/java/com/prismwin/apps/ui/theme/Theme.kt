package com.prismwin.apps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF74D8CD),
    onPrimary = Color(0xFF003733),
    primaryContainer = Color(0xFF05514C),
    secondary = Color(0xFFA6C9E8),
    secondaryContainer = Color(0xFF183550),
    background = Color(0xFF0F161D),
    surface = Color(0xFF17202A),
    surfaceVariant = Color(0xFF253646),
    onSurfaceVariant = Color(0xFFD0DEE8)
)

@Composable
fun PrismTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
