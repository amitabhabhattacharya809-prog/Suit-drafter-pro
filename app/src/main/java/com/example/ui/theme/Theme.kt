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
    primary = TailorGoldLight,
    onPrimary = TailorNavyDark,
    primaryContainer = TailorNavyContainer,
    onPrimaryContainer = TailorChalk,
    secondary = TailorGold,
    onSecondary = TailorNavyDark,
    secondaryContainer = TailorGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = TailorChalkLine,
    background = TailorNavyDark,
    surface = Color(0xFF141E2E),
    surfaceVariant = Color(0xFF1F2B3F),
    onBackground = TailorChalk,
    onSurface = TailorChalk,
    onSurfaceVariant = Color(0xFFC0CAD6),
    outline = Color(0xFF4C5E78)
)

private val LightColorScheme = lightColorScheme(
    primary = TailorNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4ECF7),
    onPrimaryContainer = TailorNavyDark,
    secondary = TailorGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBEFD8),
    onSecondaryContainer = Color(0xFF382305),
    tertiary = TailorSlate,
    background = TailorParchment,
    surface = TailorParchmentSurface,
    surfaceVariant = Color(0xFFEBF0F6),
    onBackground = TailorCharcoal,
    onSurface = TailorCharcoal,
    onSurfaceVariant = Color(0xFF434E5C),
    outline = Color(0xFF8692A3)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our tailored bespoke palette by default for strong identity
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
