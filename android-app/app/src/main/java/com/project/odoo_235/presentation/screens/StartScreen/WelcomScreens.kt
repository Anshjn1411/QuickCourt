package com.project.odoo_235.presentation.screens.StartScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.ui.theme.AppColors

@Composable
fun OnboardingScreen1(navController: NavController) {
    OnboardingLayout(
        navController = navController,
        currentPage = 0,
        illustration = "📍",
        title = "Find Courts Near You",
        subtitle = "Discover badminton, tennis, and other sports facilities in your area with real-time availability.",
        onNext = { navController.navigate("Onboarding2") },
        onSkip = { navController.navigate(Screen.SignUp.routes) }
    )
}

@Composable
fun OnboardingScreen2(navController: NavController) {
    OnboardingLayout(
        navController = navController,
        currentPage = 1,
        illustration = "⚡",
        title = "Book Instantly",
        subtitle = "Reserve your preferred court in seconds with secure payments and instant confirmation.",
        onNext = { navController.navigate("Onboarding3") },
        onBack = { navController.popBackStack() }
    )
}

@Composable
fun OnboardingScreen3(navController: NavController) {
    OnboardingLayout(
        navController = navController,
        currentPage = 2,
        illustration = "📅",
        title = "Manage Your Bookings",
        subtitle = "Track your reservations, get reminders, and reschedule anytime with ease.",
        onNext = { navController.navigate(Screen.SignUp.routes) },
        onBack = { navController.popBackStack() },
        isLastPage = true
    )
}


@Composable
private fun OnboardingLayout(
    navController: NavController,
    currentPage: Int,
    illustration: String,
    title: String,
    subtitle: String,
    onNext: () -> Unit,
    onBack: (() -> Unit)? = null,
    onSkip: (() -> Unit)? = null,
    isLastPage: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppColors.Primary.copy(alpha = 0.05f),
                        AppColors.Background,
                        AppColors.Primary.copy(alpha = 0.02f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Section with Skip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                if (onSkip != null) {
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            "Skip",
                            color = AppColors.OnSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Main Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Illustration Container
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    AppColors.Primary.copy(alpha = 0.1f),
                                    AppColors.Primary.copy(alpha = 0.05f),
                                    AppColors.Background
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner circle with gradient
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.Primary.copy(alpha = 0.2f),
                                        AppColors.PrimaryVariant.copy(alpha = 0.15f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = illustration,
                            fontSize = 48.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Title
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = subtitle,
                    fontSize = 16.sp,
                    color = AppColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Page Indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (index == currentPage) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (index == currentPage) AppColors.Primary
                                else AppColors.Primary.copy(alpha = 0.3f)
                            )
                    )
                    if (index < 2) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (onBack != null) Arrangement.SpaceBetween else Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                if (onBack != null) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .height(48.dp)
                            .width(100.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.Primary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(AppColors.Primary, AppColors.PrimaryVariant)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Back",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Next/Get Started Button
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .height(48.dp)
                        .width(if (isLastPage) 140.dp else 100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        if (isLastPage) "Get Started" else "Next",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}