package com.project.odoo_235.presentation.screens.user.screen.components

import androidx.compose.animation.VectorConverter
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.presentation.screens.AutheScreen.AuthViewModel
import com.project.odoo_235.ui.theme.AppColors
import com.project.odoo_235.ui.theme.AppColors1
import com.project.odoo_235.ui.theme.Odoo_235Theme


@Composable
fun QuickCourtBottomBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Booking,
        BottomNavItem.ChatBot,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = AppColors.Surface,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(
                    painter = painterResource(id = item.icon), // iconRes = Int
                    contentDescription = item.title,
                    modifier = Modifier.size(35.dp)
                ) },
                label = { Text(item.title, fontSize = 12.sp) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors1.SportsDark,
                    selectedTextColor = AppColors1.SportsDark,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black
                )
            )
        }
    }
}

