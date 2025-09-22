package com.project.odoo_235.presentation.screens.user.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.HomeScreen
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.MainViewModel
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.BookingViewModel
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.MyBookingsScreen
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.UserBookingAnalyticsScreen
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.BookingSuccessScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType
import ProfileScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.project.odoo_235.data.datastore.UserSessionManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.CourtsListScreen
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.BookingScreenNewUI
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.PaymentModeScreen
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.BookingDetailsScreen
import com.project.odoo_235.presentation.screens.user.screen.bookingScreen.VenueDetailScreen
import com.project.odoo_235.presentation.screens.user.screen.livematches.CricketMatchListScreen
import com.project.odoo_235.presentation.screens.user.screen.livematches.CricketMatchDetailScreen
import com.project.odoo_235.presentation.screens.user.screen.livematches.CricketAdminPanel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserNavHost() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val bookingViewModel: BookingViewModel = viewModel()
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    NavHost(navController = navController, startDestination = Screen.MainDashBoard.routes) {
        composable(Screen.MainDashBoard.routes) { HomeScreen(navController, mainViewModel) }
        composable(Screen.Courts.routes) { CourtsListScreen(navController, mainViewModel) }
        composable(Screen.MyBookings.routes) { MyBookingsScreen(navController, bookingViewModel , mainViewModel) }
        composable(
            route = "booking_details/{bookingId}",
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailsScreen(navController, bookingViewModel, bookingId)
        }
        composable(Screen.UserBookingAnalytics.routes) { UserBookingAnalyticsScreen(navController, bookingViewModel) }

        composable(
            route = Screen.BookingScreen.routes,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            VenueDetailScreen(
                venueId = bookingId,
                venueViewModel = bookingViewModel,
                navController=navController,
                onGetDirectionClick = { },
                onCallClick = { }
            )
        }

        composable(
            route = "booking_select/{venueId}",
            arguments = listOf(navArgument("venueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val venueId = backStackEntry.arguments?.getString("venueId") ?: ""
            val venue by bookingViewModel.venueDetail.collectAsState()
            if (venue != null && venue!!.id == venueId) {
                BookingScreenNewUI(
                    venue = venue!!,
                    bookingViewModel = bookingViewModel,
                    onBooked = {
                        navController.navigate("payment_mode")
                    }
                )
            } else {
                // Ensure venue details are loaded, then show when ready
                bookingViewModel.fetchVenueDetail(venueId)
            }
        }

        composable(
            route = "payment_mode"
        ) {
            PaymentModeScreen(
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.bookingSucess.routes) {
                        popUpTo(Screen.MainDashBoard.routes) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.bookingSucess.routes) {
            BookingSuccessScreen(
                bookingViewModel = bookingViewModel,
                onGoHome = {
                    navController.navigate(Screen.MainDashBoard.routes) {
                        popUpTo(Screen.MainDashBoard.routes) { inclusive = true }
                    }
                },
                onViewReceipt = {}
            )
        }

        composable(route = "profile") {
            ProfileScreen(
                navController,mainViewModel,
                userSessionManager = sessionManager,
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.routes) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Cricket Match Screens
        composable(Screen.CricketMatchList.routes) {
            CricketMatchListScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate(Screen.CricketMatchDetail.createRoute(matchId))
                }
            )
        }

        composable(
            route = Screen.CricketMatchDetail.routes,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            CricketMatchDetailScreen(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CricketAdminPanel.routes,
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            CricketAdminPanel(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}


