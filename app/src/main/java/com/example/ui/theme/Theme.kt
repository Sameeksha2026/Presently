package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigoLight,
    onPrimary = Color(0xFF0F172A),
    primaryContainer = PrimaryIndigoDark,
    onPrimaryContainer = PrimaryIndigoContainer,
    secondary = SecondaryVioletLight,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF5B21B6),
    onSecondaryContainer = SecondaryVioletContainer,
    tertiary = TertiaryCyan,
    onTertiary = Color(0xFF0F172A),
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDarkElevated,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF243048),
    onSurfaceVariant = TextSecondaryDark,
    outline = SurfaceDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigoContainer,
    onPrimaryContainer = PrimaryIndigoDark,
    secondary = SecondaryViolet,
    onSecondary = Color.White,
    secondaryContainer = SecondaryVioletContainer,
    onSecondaryContainer = Color(0xFF4C1D95),
    tertiary = TertiaryCyan,
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLightCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = SurfaceLightBorder
)

@Composable
fun PresentlyAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature brand palette by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
