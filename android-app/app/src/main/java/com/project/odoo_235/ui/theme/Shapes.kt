package com.project.odoo_235.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material Design 3 Shape System for Sports Facility App
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),      // For small elements like chips, badges
    small = RoundedCornerShape(8.dp),           // For buttons, input fields
    medium = RoundedCornerShape(12.dp),         // For cards, dialogs
    large = RoundedCornerShape(16.dp),          // For sheets, large cards
    extraLarge = RoundedCornerShape(28.dp)      // For bottom sheets, large containers
)

// Extended Shape System for Sports App Components
object AppShapes {
    // Button Shapes
    val buttonSmall = RoundedCornerShape(6.dp)
    val buttonMedium = RoundedCornerShape(8.dp)
    val buttonLarge = RoundedCornerShape(12.dp)
    val buttonPill = RoundedCornerShape(50)        // Fully rounded buttons

    // Card Shapes
    val cardSmall = RoundedCornerShape(8.dp)
    val cardMedium = RoundedCornerShape(12.dp)
    val cardLarge = RoundedCornerShape(16.dp)
    val cardExtraLarge = RoundedCornerShape(20.dp)

    // Input Field Shapes
    val inputField = RoundedCornerShape(8.dp)
    val inputFieldLarge = RoundedCornerShape(12.dp)
    val searchBar = RoundedCornerShape(24.dp)      // Rounded search bars

    // Dialog & Modal Shapes
    val dialog = RoundedCornerShape(16.dp)
    val bottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val topSheet = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )

    // Chip & Badge Shapes
    val chipSmall = RoundedCornerShape(12.dp)
    val chipMedium = RoundedCornerShape(16.dp)
    val chipLarge = RoundedCornerShape(20.dp)
    val badge = RoundedCornerShape(10.dp)
    val statusBadge = RoundedCornerShape(6.dp)

    // Image & Avatar Shapes
    val avatarSmall = RoundedCornerShape(16.dp)
    val avatarMedium = RoundedCornerShape(24.dp)
    val avatarLarge = RoundedCornerShape(32.dp)
    val imageSmall = RoundedCornerShape(8.dp)
    val imageMedium = RoundedCornerShape(12.dp)
    val imageLarge = RoundedCornerShape(16.dp)

    // Sports Facility Specific Shapes
    val facilityCard = RoundedCornerShape(16.dp)
    val bookingCard = RoundedCornerShape(12.dp)
    val timeSlot = RoundedCornerShape(8.dp)
    val priceCard = RoundedCornerShape(10.dp)

    // Navigation Shapes
    val navigationBar = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    val tabIndicator = RoundedCornerShape(16.dp)

    // Progress & Loading Shapes
    val progressBar = RoundedCornerShape(4.dp)
    val loadingIndicator = RoundedCornerShape(2.dp)

    // Special Shapes
    val none = RoundedCornerShape(0.dp)            // No rounding
    val circle = RoundedCornerShape(50)            // Perfect circle
//    val semicircle = RoundedCornerShape(
//        topStart = 50,
//        topEnd = 50,
//        bottomStart = 0.dp,
//        bottomEnd = 0.dp
//    )

    // Container Shapes for different sections
    val headerContainer = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )
    val footerContainer = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Notification & Alert Shapes
    val notification = RoundedCornerShape(12.dp)
    val alert = RoundedCornerShape(8.dp)
    val snackbar = RoundedCornerShape(4.dp)

    // Form Component Shapes
    val formSection = RoundedCornerShape(12.dp)
    val formGroup = RoundedCornerShape(8.dp)
    val radioButton = RoundedCornerShape(50)
    val checkbox = RoundedCornerShape(4.dp)
    val toggle = RoundedCornerShape(16.dp)
}

// Shape variants for different states
object AppShapeVariants {
    // Hover/Focus states with slightly larger radius
    object Hover {
        val buttonSmall = RoundedCornerShape(8.dp)
        val buttonMedium = RoundedCornerShape(10.dp)
        val buttonLarge = RoundedCornerShape(14.dp)
        val cardSmall = RoundedCornerShape(10.dp)
        val cardMedium = RoundedCornerShape(14.dp)
        val cardLarge = RoundedCornerShape(18.dp)
    }

    // Pressed states with slightly smaller radius for pressed effect
    object Pressed {
        val buttonSmall = RoundedCornerShape(4.dp)
        val buttonMedium = RoundedCornerShape(6.dp)
        val buttonLarge = RoundedCornerShape(10.dp)
        val cardSmall = RoundedCornerShape(6.dp)
        val cardMedium = RoundedCornerShape(10.dp)
        val cardLarge = RoundedCornerShape(14.dp)
    }
}

// Responsive shape system based on screen size
object ResponsiveShapes {
    // Small screens (phones)
    object Small {
        val card = RoundedCornerShape(8.dp)
        val button = RoundedCornerShape(6.dp)
        val dialog = RoundedCornerShape(12.dp)
        val bottomSheet = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }

    // Medium screens (small tablets)
    object Medium {
        val card = RoundedCornerShape(12.dp)
        val button = RoundedCornerShape(8.dp)
        val dialog = RoundedCornerShape(16.dp)
        val bottomSheet = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }

    // Large screens (tablets, desktop)
    object Large {
        val card = RoundedCornerShape(16.dp)
        val button = RoundedCornerShape(12.dp)
        val dialog = RoundedCornerShape(20.dp)
        val bottomSheet = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }
}