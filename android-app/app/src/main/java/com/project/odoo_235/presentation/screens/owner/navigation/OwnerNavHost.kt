package com.project.odoo_235.presentation.screens.owner.navigation

import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.AutheScreen.AuthViewModel
import com.project.odoo_235.presentation.screens.AutheScreen.LoginScreen
import com.project.odoo_235.presentation.screens.owner.mainscreen.FacilityDashboardScreen
import com.project.odoo_235.presentation.screens.owner.create.CreateCourtScreen
import com.project.odoo_235.ui.theme.md_theme_dark_inversePrimary

@Composable
fun OwnerNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }
    val authVm: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(sessionManager) as T
            }
        }
    )

    NavHost(navController = navController, startDestination = Screen.MainDashBoard.routes) {
        composable(Screen.MainDashBoard.routes) { FacilityDashboardScreen(navController) }
        composable(Screen.Add.routes) {
            CreateCourtScreen(
                onCourtCreated = { navController.navigate(Screen.MainDashBoard.routes) },
                onBackPressed = { navController.navigate(Screen.MainDashBoard.routes) }
            )
        }
        composable(Screen.Profile.routes) {
           // ProfileScreen(sessionManager , mainViewModel = ) { navController.navigate(Screen.Login.routes) }
        }
        composable(Screen.Login.routes) { LoginScreen(navController, authViewModel = authVm) }
    }
}


