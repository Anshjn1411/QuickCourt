package com.project.odoo_235.ui.theme

import androidx.compose.ui.graphics.Color

// Sports Facility App Theme - Green Primary with Material Design 3

// Light Theme Colors
val md_theme_light_primary = Color(0xFF00C896)              // Vibrant green from your app
val md_theme_light_onPrimary = Color(0xFFFFFFFF)            // White text on green
val md_theme_light_primaryContainer = Color(0xFFE0F7F0)      // Very light green container
val md_theme_light_onPrimaryContainer = Color(0xFF00512C)    // Dark green text on light container
val md_theme_light_secondary = Color(0xFF52796F)            // Muted teal-green
val md_theme_light_onSecondary = Color(0xFFFFFFFF)          // White text on secondary
val md_theme_light_secondaryContainer = Color(0xFFD4EDDA)    // Light teal-green container
val md_theme_light_onSecondaryContainer = Color(0xFF0F2922)  // Dark teal text
val md_theme_light_tertiary = Color(0xFF4A90E2)             // Blue accent
val md_theme_light_onTertiary = Color(0xFFFFFFFF)           // White text on tertiary
val md_theme_light_tertiaryContainer = Color(0xFFE3F2FD)    // Light blue container
val md_theme_light_onTertiaryContainer = Color(0xFF0D47A1)  // Dark blue text
val md_theme_light_error = Color(0xFFE53935)               // Red error
val md_theme_light_errorContainer = Color(0xFFFFEBEE)       // Light red container
val md_theme_light_onError = Color(0xFFFFFFFF)             // White text on error
val md_theme_light_onErrorContainer = Color(0xFFB71C1C)     // Dark red text
val md_theme_light_background = Color(0xFFFFFFFE)           // Pure white background
val md_theme_light_onBackground = Color(0xFF1A1C18)         // Dark text on background
val md_theme_light_surface = Color(0xFFF8FDF9)             // Very light green-tinted surface
val md_theme_light_onSurface = Color(0xFF1A1C18)           // Dark text on surface
val md_theme_light_surfaceVariant = Color(0xFFE8F5E8)      // Light green surface variant
val md_theme_light_onSurfaceVariant = Color(0xFF404943)     // Medium dark text
val md_theme_light_outline = Color(0xFF70796C)             // Green-tinted outline
val md_theme_light_inverseOnSurface = Color(0xFFF0F1EC)    // Light text for inverse
val md_theme_light_inverseSurface = Color(0xFF2F312C)      // Dark surface for inverse
val md_theme_light_inversePrimary = Color(0xFF7DDBBB)      // Light green for dark backgrounds
val md_theme_light_shadow = Color(0xFF000000)              // Black shadow
val md_theme_light_surfaceTint = Color(0xFF00C896)         // Primary color tint
val md_theme_light_outlineVariant = Color(0xFFC1CDB6)      // Light green outline variant
val md_theme_light_scrim = Color(0xFF000000)               // Black scrim

// Dark Theme Colors
val md_theme_dark_primary = Color(0xFF7DDBBB)              // Light green for dark mode
val md_theme_dark_onPrimary = Color(0xFF00512C)            // Dark green text on primary
val md_theme_dark_primaryContainer = Color(0xFF00724A)      // Medium green container
val md_theme_dark_onPrimaryContainer = Color(0xFFE0F7F0)   // Light green text
val md_theme_dark_secondary = Color(0xFFB8CCB8)            // Light muted green
val md_theme_dark_onSecondary = Color(0xFF243B2C)          // Dark text on secondary
val md_theme_dark_secondaryContainer = Color(0xFF3A5241)   // Dark green container
val md_theme_dark_onSecondaryContainer = Color(0xFFD4EDDA) // Light teal text
val md_theme_dark_tertiary = Color(0xFF90CAF9)             // Light blue for dark mode
val md_theme_dark_onTertiary = Color(0xFF0D47A1)           // Dark blue text
val md_theme_dark_tertiaryContainer = Color(0xFF1976D2)    // Medium blue container
val md_theme_dark_onTertiaryContainer = Color(0xFFE3F2FD)  // Light blue text
val md_theme_dark_error = Color(0xFFFF5722)               // Orange-red error
val md_theme_dark_errorContainer = Color(0xFFB71C1C)       // Dark red container
val md_theme_dark_onError = Color(0xFFFFFFFF)             // White text on error
val md_theme_dark_onErrorContainer = Color(0xFFFFCDD2)     // Light red text
val md_theme_dark_background = Color(0xFF0F1411)           // Very dark green-black
val md_theme_dark_onBackground = Color(0xFFE1E3DE)         // Light text on background
val md_theme_dark_surface = Color(0xFF101411)             // Dark green-tinted surface
val md_theme_dark_onSurface = Color(0xFFE1E3DE)           // Light text on surface
val md_theme_dark_surfaceVariant = Color(0xFF404943)       // Dark green surface variant
val md_theme_dark_onSurfaceVariant = Color(0xFFC1CDB6)     // Light green text
val md_theme_dark_outline = Color(0xFF8B9285)             // Medium green outline
val md_theme_dark_inverseOnSurface = Color(0xFF1A1C18)     // Dark text for inverse
val md_theme_dark_inverseSurface = Color(0xFFE1E3DE)       // Light surface for inverse
val md_theme_dark_inversePrimary = Color(0xFF00C896)       // Primary color for light backgrounds
val md_theme_dark_shadow = Color(0xFF000000)              // Black shadow
val md_theme_dark_surfaceTint = Color(0xFF7DDBBB)         // Light green tint
val md_theme_dark_outlineVariant = Color(0xFF404943)       // Dark green outline variant
val md_theme_dark_scrim = Color(0xFF000000)               // Black scrim

// Custom App Colors for Sports Facility Theme
object AppColors1 {
    // Primary Colors - Green Theme
    val SportsPrimary = Color(0xFF00C896)           // Main green from your app
    val SportsAccent = Color(0xFF00E5A0)            // Lighter accent green
    val SportsDark = Color(0xFF00BE76)
    // Darker green for variants
    val SportsLight = Color(0xFF7DDBBB)             // Light green for dark mode

    // Status Colors for Bookings
    val BookingConfirmed = Color(0xFF00C896)        // Green for confirmed bookings
    val BookingPending = Color(0xFFFF9800)          // Orange for pending
    val BookingCancelled = Color(0xFFE53935)        // Red for cancelled
    val BookingAvailable = Color(0xFF4CAF50)        // Different green for available slots

    // Facility Type Colors
    val CricketGreen = Color(0xFF2E7D32)            // Cricket field green
    val FootballGreen = Color(0xFF388E3C)           // Football field green
    val TennisGreen = Color(0xFF43A047)             // Tennis court green
    val BasketballOrange = Color(0xFFFF6F00)        // Basketball court color

    // Background & Surface Colors
    val LightBackground = Color(0xFFFFFFFE)         // Pure white
    val LightSurface = Color(0xFFF8FDF9)           // Very light green-tinted
    val LightSurfaceVariant = Color(0xFFE8F5E8)     // Light green surface

    val DarkBackground = Color(0xFF0F1411)          // Very dark green-black
    val DarkSurface = Color(0xFF101411)            // Dark green-tinted
    val DarkSurfaceVariant = Color(0xFF1A1F1B)      // Slightly lighter dark green

    // Text Colors
    val TextOnPrimary = Color(0xFFFFFFFF)           // White text on green
    val TextOnBackground = Color(0xFF1A1C18)        // Dark text on light background
    val TextOnSurface = Color(0xFF1A1C18)          // Dark text on light surface
    val TextSecondary = Color(0xFF5F6368)           // Gray text for secondary info
    val TextDisabled = Color(0xFF9AA0A6)            // Disabled text color

    // Dark mode text
    val TextOnBackgroundDark = Color(0xFFE1E3DE)    // Light text on dark background
    val TextOnSurfaceDark = Color(0xFFE1E3DE)      // Light text on dark surface
    val TextSecondaryDark = Color(0xFFBDC1C6)       // Light gray for secondary info
    val TextDisabledDark = Color(0xFF80868B)        // Disabled text for dark mode

    // Border & Outline Colors
    val BorderLight = Color(0xFFE0E0E0)             // Light border
    val BorderDark = Color(0xFF5F6368)              // Dark border
    val OutlineLight = Color(0xFF70796C)            // Green-tinted outline
    val OutlineDark = Color(0xFF8B9285)             // Light green outline for dark mode

    // Gradient Colors
    val TopBarStart = Color(0xFFEAFDF5) // #EAFDF5 (very light mint)
    val TopBarMid   = Color(0xFFD6FAEE) // #D6FAEE
    val TopBarEnd   = Color(0xFFC3F7E7) // #C3F7E7 (slightly deeper mint)

    // Card & Component Colors
    val CardBackground = Color(0xFFFFFFFF)          // White card background
    val CardBackgroundDark = Color(0xFF1E1E1E)      // Dark card background
    val CardElevation = Color(0x1A000000)           // Subtle shadow for cards

    // Button Colors
    val ButtonPrimary = SportsPrimary
    val ButtonSecondary = Color(0xFF6C757D)         // Gray button
    val ButtonSuccess = BookingConfirmed            // Green success button
    val ButtonWarning = BookingPending              // Orange warning button
    val ButtonDanger = BookingCancelled             // Red danger button

    // Utility Colors
    val SuccessGreen = Color(0xFF28A745)            // Success messages
    val WarningAmber = Color(0xFFFFC107)            // Warning messages
    val InfoBlue = Color(0xFF17A2B8)               // Info messages
    val DangerRed = Color(0xFFDC3545)              // Error/danger messages

    // Rating & Review Colors
    val StarGold = Color(0xFFFFB000)               // Star rating color
    val StarGray = Color(0xFFE0E0E0)               // Empty star color

    // Price & Currency Colors
    val PriceGreen = SportsPrimary                  // Price in green theme
    val DiscountRed = Color(0xFFE53935)            // Discount/sale price
    val CurrencyText = Color(0xFF424242)            // Currency symbol color

    // Time & Date Colors
    val TimeSlotAvailable = Color(0xFFE8F5E8)       // Available time slot background
    val TimeSlotBooked = Color(0xFFFFEBEE)          // Booked time slot background
    val TimeSlotSelected = Color(0xFFE0F7F0)        // Selected time slot background
}

// Legacy colors for backward compatibility
val Purple80 = md_theme_dark_primary
val PurpleGrey80 = md_theme_dark_secondary
val Pink80 = md_theme_dark_tertiary

val Purple40 = md_theme_light_primary
val PurpleGrey40 = md_theme_light_secondary
val Pink40 = md_theme_light_tertiary