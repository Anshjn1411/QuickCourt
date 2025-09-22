// app/src/main/java/.../presentation/auth/LoginScreen.kt
package com.project.odoo_235.presentation.screens.AutheScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Facebook
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.R
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.StartScreen.SplashScreen
import com.project.odoo_235.ui.theme.AppColors
import com.project.odoo_235.ui.theme.AppColors1
import com.project.odoo_235.ui.theme.Odoo_235Theme
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loading by authViewModel.loading
    val loginSuccess by authViewModel.successLogin
    val loginMessage by authViewModel.loginMessage
    val role by authViewModel.role

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            if (role == "Owner") navController.navigate(Screen.MainDashBoard.routes) else navController.navigate(Screen.MainDashBoard.routes)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Title + Subtitle
            Text(
                text = "Login",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors1.SportsDark
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Have Fun with Friends!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = AppColors1.SportsDark
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20), // 40 dp round corners
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors1.SportsDark,   // Green border when focused
                    unfocusedBorderColor = AppColors1.SportsAccent, // Green border when not focused
                    cursorColor = Color.Black
                )
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Password,
                        contentDescription = null,
                        tint = AppColors1.SportsDark
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20), // 40 dp round corners
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors1.SportsDark,   // Green border when focused
                    unfocusedBorderColor = AppColors1.SportsAccent, // Green border when not focused
                    cursorColor = Color.Black
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Visibility
                            else Icons.Outlined.VisibilityOff,
                            contentDescription = null,
                            tint = AppColors1.SportsDark
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Forgot Password
            TextButton(
                onClick = { navController.navigate("ForgotPassword") },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Forgot Password?" ,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppColors1.SportsDark)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = { authViewModel.login(email, password) },
                enabled = !loading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors1.SportsDark,       // Green background (enabled)
                    contentColor = Color.White,                   // White text
                    disabledContainerColor = AppColors1.SportsDark.copy(alpha = 0.6f), // Green but lighter when disabled
                    disabledContentColor = Color.White.copy(alpha = 0.8f)              // White faded text
                )
            ) {
                Text(
                    text = if (loading) "Signing in..." else "Login",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Message (error/success)
            if (!loginMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(loginMessage!!, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OR Divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text("  OR  ", color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { /* Google login */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        tint = Color.Unspecified // keeps original logo colors
                    )
                }

                IconButton(onClick = { /* Facebook login */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook),
                        contentDescription = "Facebook",
                        tint = Color.Unspecified
                    )
                }

                IconButton(onClick = { /* Apple login */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.apple),
                        contentDescription = "Apple",
                        tint = Color.Unspecified
                    )
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Don't have an account?",
                        style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                        ))


                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AppColors1.SportsDark, // your #00BE76 green
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.SignUp.routes)
                    }
                )
            }

        }
    }
}

