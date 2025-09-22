package com.project.odoo_235.presentation.screens.admin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.admin.screens.AdminDashboardScreen

@Composable
fun AdminNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.AdminDashboard.routes) {
        composable(Screen.AdminDashboard.routes) {
            AdminDashboardScreen()
        }
    }
}


