package com.project.odoo_235.presentation.screens.owner.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerTopBar(
    currentLocation: String = "Mumbai",
    onProfileClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "QuickCourt",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.Primary
        ),
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White
                )
            }
        }
    )
}

// ---------- BOTTOM NAV BAR ----------
sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Home", Icons.Default.Home, Screen.MainDashBoard.routes)
    object ADD : BottomNavItem("Explore", Icons.Default.Add, Screen.Add.routes)
    object Bookings : BottomNavItem("Bookings", Icons.Default.CalendarToday, Screen.booking.routes)
    object Profile : BottomNavItem("Profile", Icons.Default.Person, Screen.Profile.routes)
}
