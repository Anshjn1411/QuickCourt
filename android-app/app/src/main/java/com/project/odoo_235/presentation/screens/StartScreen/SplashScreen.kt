package com.project.odoo_235.presentation.screens.StartScreen

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.AutheScreen.AuthViewModel
import com.project.odoo_235.ui.theme.AppColors
import com.project.odoo_235.ui.theme.Odoo_235Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import com.project.odoo_235.R
import com.project.odoo_235.ui.theme.AppColors1

@Composable
fun SplashScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager(context) }

    LaunchedEffect(Unit) {
        delay(1500) // slightly faster splash

        val isLoggedIn = runCatching { sessionManager.checkIsLoggedIn() }.getOrDefault(false)
        val cachedUser = runCatching { sessionManager.getUser() }.getOrNull()

        if (isLoggedIn && cachedUser != null && cachedUser.email.isNotBlank()) {
            when (cachedUser.role) {
                "Owner" -> {
                    navController.navigate(Screen.Navigation.routes) {
                        popUpTo(Screen.SplashScreen.routes) { inclusive = true }
                    }
                }
                "Admin" -> {
                    navController.navigate(Screen.AdminNavigation.routes) {
                        popUpTo(Screen.SplashScreen.routes) { inclusive = true }
                    }
                }
                else -> {
                    navController.navigate(Screen.UserNavigation.routes) {
                        popUpTo(Screen.SplashScreen.routes) { inclusive = true }
                    }
                }
            }
        } else {
            navController.navigate(Screen.WelcomeScreen1.routes) {
                popUpTo(Screen.SplashScreen.routes) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Top Texts
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GEAR UP",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors1.SportsDark, // Green shade
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "A BIG GAME",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color =AppColors1.SportsDark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Have Fun with Friends!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Middle Image
            Image(
                painter = painterResource(id = R.drawable.splash), // replace with your Splash.png in drawable
                contentDescription = "Cricket Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

        }
    }
}

