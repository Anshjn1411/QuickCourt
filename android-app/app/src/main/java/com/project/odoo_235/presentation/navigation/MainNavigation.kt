package com.project.odoo_235.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.project.odoo_235.data.datastore.UserSessionManager

import com.project.odoo_235.presentation.screens.AutheScreen.AuthViewModel
import com.project.odoo_235.presentation.screens.AutheScreen.LoginScreen
import com.project.odoo_235.presentation.screens.AutheScreen.*
import com.project.odoo_235.presentation.screens.AutheScreen.SignUpScreen
import com.project.odoo_235.presentation.screens.owner.navigation.OwnerNavHost
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.MainViewModel
import com.project.odoo_235.presentation.screens.StartScreen.OnboardingScreen1
import com.project.odoo_235.presentation.screens.StartScreen.OnboardingScreen2
import com.project.odoo_235.presentation.screens.StartScreen.OnboardingScreen3
import com.project.odoo_235.presentation.screens.StartScreen.SplashScreen
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.BookingViewModel
import com.project.odoo_235.presentation.screens.admin.navigation.AdminNavHost
import com.project.odoo_235.presentation.screens.user.navigation.UserNavHost
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(nav: NavHostController) {
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
    val mainViewModel: MainViewModel = viewModel()
    val bookingViewModel: BookingViewModel = viewModel()

    NavHost(navController = nav, startDestination = Screen.SplashScreen.routes) {
        composable(Screen.SplashScreen.routes) {
            SplashScreen(navController = nav)
        }
        composable(Screen.Navigation.routes) { OwnerNavHost() }
        composable(Screen.AdminNavigation.routes) { AdminNavHost() }
        composable(Screen.UserNavigation.routes) { UserNavHost() }
        composable(Screen.WelcomeScreen1.routes) { OnboardingScreen1(nav) }
        composable("Onboarding2") { OnboardingScreen2(nav) }
        composable("Onboarding3") { OnboardingScreen3(nav) }

        composable(Screen.Login.routes) { LoginScreen(nav, authVm) }
        composable(Screen.SignUp.routes) { SignUpScreen(nav, authVm) }
        composable("ResendVerification?email={email}", arguments = listOf(navArgument("email") { type = NavType.StringType; nullable = true; defaultValue = "" })) {
            val email = it.arguments?.getString("email").orEmpty()
            ResendVerificationScreen(authVm, email) { nav.popBackStack() }
        }
        composable("VerifyEmail/{token}", arguments = listOf(navArgument("token") { type = NavType.StringType })) {
            val token = it.arguments?.getString("token")!!
            VerifyEmailScreen(token, authVm) { nav.navigate(Screen.Login.routes) { popUpTo(Screen.Login.routes) { inclusive = true } } }
        }
        composable("ForgotPassword") { ForgotPasswordScreen(authVm) { email -> nav.navigate("ResendVerification?email=$email") } }
        composable("ResetPassword/{token}", arguments = listOf(navArgument("token") { type = NavType.StringType })) {
            val token = it.arguments?.getString("token")!!
            ResetPasswordScreen(token, authVm) { nav.navigate(Screen.Login.routes) { popUpTo(Screen.Login.routes) { inclusive = true } } }
        }
        composable("UpdatePassword") { UpdatePasswordScreen(authVm) { nav.popBackStack() } }

        // Role graphs
        composable(Screen.Navigation.routes) { OwnerNavHost() }
        composable(Screen.AdminNavigation.routes) { AdminNavHost() }
        composable(Screen.UserNavigation.routes) { UserNavHost() }
    }
}