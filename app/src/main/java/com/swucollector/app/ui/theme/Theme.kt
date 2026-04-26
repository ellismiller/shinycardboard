package com.swucollector.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SWUYellow,
    onPrimary = SWUDarkBlue,
    primaryContainer = SWUBlue,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9DB8D2),
    onSecondary = SWUDarkBlue,
    background = SWUDarkBlue,
    onBackground = Color.White,
    surface = SWUSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E2E40),
    onSurfaceVariant = Color(0xFFBBCCDD),
    error = SWURed,
    onError = Color.White
)

@Composable
fun ShinyCardboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
