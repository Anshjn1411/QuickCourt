package com.project.odoo_235.presentation.screens.user.screen.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.ui.theme.AppColors1
import com.project.odoo_235.R
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.MainViewModel

@Composable
fun QuickCourtTopBar(
    userName: String,
    location: String,
    isLocationLoading: Boolean,
    onRefreshLocation: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors1.TopBarEnd,
                        AppColors1.TopBarMid,
                        AppColors1.TopBarStart
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Location",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AppColors1.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onRefreshLocation() }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(2.dp))

                    if (isLocationLoading) {
                        ShimmerLocationText()
                    } else {
                        Text(
                            text = location.ifEmpty { "Unknown Location" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Right section → Welcome Back + Name
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "WELCOME BACK!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AppColors1.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = userName.ifEmpty { "User" },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun ShimmerLocationText() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = -200f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer_translate"
    )

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Gray.copy(alpha = 0.3f),
                        Color.Gray.copy(alpha = 0.5f),
                        Color.Gray.copy(alpha = 0.3f)
                    ),
                    startX = translateAnim.value - 100f,
                    endX = translateAnim.value + 100f
                )
            )
    )
}

// Alternative shimmer effect with more realistic loading text
@Composable
fun ShimmerLocationTextWithDots() {
    val transition = rememberInfiniteTransition(label = "loading_dots")
    val animatedDots = transition.animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "loading_dots"
    )

    Text(
        text = "Loading" + ".".repeat(animatedDots.value + 1),
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    )
}






// ---------- BOTTOM NAV BAR ----------
sealed class BottomNavItem(val title: String, val icon: Int, val route: String) {
    object Home : BottomNavItem("Home", R.drawable.ic_home, Screen.MainDashBoard.routes)
    object Explore : BottomNavItem("Court", R.drawable.ic_sports_cricket, Screen.Courts.routes)
    object Booking : BottomNavItem("Bookings", R.drawable.ic_booking, Screen.MyBookings.routes)

    object ChatBot : BottomNavItem("AI", R.drawable.ic_chatbot, "bot")
    object Profile : BottomNavItem("Profile", R.drawable.outline_person_24, Screen.Profile.routes)
}
