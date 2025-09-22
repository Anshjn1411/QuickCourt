package com.project.odoo_235.presentation.screens.owner.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.odoo_235.ui.theme.AppColors

@Composable
fun OwnerBottomBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.ADD,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = AppColors.Surface,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, fontSize = 12.sp) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.Primary,
                    selectedTextColor = AppColors.Primary,
                    unselectedIconColor = AppColors.OnSurfaceVariant,
                    unselectedTextColor = AppColors.OnSurfaceVariant
                )
            )
        }
    }
}
