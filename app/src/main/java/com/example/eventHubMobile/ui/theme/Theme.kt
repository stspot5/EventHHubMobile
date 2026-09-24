package com.example.eventHubMobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBlue,
    
    secondary = AccentOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    
    tertiary = DarkBlue,
    onTertiary = Color.White,
    
    background = BackgroundWhite,
    onBackground = TextDark,
    
    surface = SurfaceWhite,
    onSurface = TextDark,
    
    outline = TextGrey
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = AccentOrange,
    tertiary = LightBlue,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun EventHHubMobileTheme(
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
