package com.project.odoo_235.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Material Design 3 Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

// Material Design 3 Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun Odoo_235Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


object AppColors {
    // Primary Colors
    val Primary = Color(0xFF2196F3)           // Blue 500
    val PrimaryVariant = Color(0xFF1976D2)    // Blue 700
    val PrimaryDark = Color(0xFF0D47A1)       // Blue 900 for contrast

    // Secondary Colors
    val Secondary = Color(0xFF90CAF9)         // Light Blue 300
    val SecondaryVariant = Color(0xFF64B5F6)  // Light Blue 400
    val SecondaryLight = Color(0xFFBBDEFB)    // Light Blue 100

    // Background & Surface
    val Background = Color(0xFFFFFFFF)        // White
    val Surface = Color(0xFFF0F7FF)           // Very light blue-white
    val SurfaceVariant = Color(0xFFE3F2FD)    // Light Blue 50 surface variant

    // Accent Colors
    val Success = Color(0xFF4CAF50)           // Green (keep for success)
    val Error = Color(0xFFEF5350)             // Red (keep for error)
    val Warning = Color(0xFFFFC107)           // Amber (keep for warning)
    val Info = Color(0xFF42A5F5)              // Info Blue

    // Text Colors
    val OnPrimary = Color(0xFFFFFFFF)         // White text on primary
    val OnSecondary = Color(0xFF000000)       // Black text on secondary
    val OnBackground = Color(0xFF0D47A1)      // Dark Blue text on background
    val OnSurface = Color(0xFF0D47A1)         // Dark Blue text on surface
    val OnSurfaceVariant = Color(0xFF1976D2)  // Medium blue text

    // Additional Utility Colors
    val Outline = Color(0xFF90CAF9)            // Light Blue borders
    val OutlineVariant = Color(0xFFE3F2FD)     // Subtle light blue borders
    val Scrim = Color(0x80000000)              // Semi-transparent overlay

    // Status Colors
    val Pending = Color(0xFFFFC107)            // Amber for pending status
    val Active = Color(0xFF4CAF50)             // Green for active
    val Completed = Color(0xFF2196F3)          // Blue for completed
    val Cancelled = Color(0xFFF44336)          // Red for cancelled

    // Gradient Colors
    val GradientStart = Primary
    val GradientEnd = Color(0xFF64B5F6)        // Light Blue for gradients

    // Dark Mode Colors
    object Dark {
        val Primary = Color(0xFF64B5F6)           // Light Blue 400 for dark mode
        val PrimaryVariant = Color(0xFF2196F3)    // Blue 500
        val Secondary = Color(0xFF90CAF9)          // Light Blue 300 for dark mode
        val SecondaryVariant = Color(0xFF64B5F6)   // Light Blue 400

        val Background = Color(0xFF121212)         // Dark background
        val Surface = Color(0xFF1E1E1E)            // Dark surface
        val SurfaceVariant = Color(0xFF2C2C2C)     // Darker surface variant

        val OnPrimary = Color(0xFF000000)          // Black text on primary
        val OnSecondary = Color(0xFF000000)        // Black text on secondary
        val OnBackground = Color(0xFFE3F2FD)       // Light text on dark background
        val OnSurface = Color(0xFFE3F2FD)          // Light text on dark surface
        val OnSurfaceVariant = Color(0xFFB3E5FC)   // Medium light text

        val Outline = Color(0xFF424242)            // Dark border lines
        val OutlineVariant = Color(0xFF2C2C2C)     // Subtle dark borders
    }
}


