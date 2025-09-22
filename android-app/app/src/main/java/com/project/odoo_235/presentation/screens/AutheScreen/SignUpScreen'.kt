// app/src/main/java/.../presentation/auth/SignUpScreen.kt
package com.project.odoo_235.presentation.screens.AutheScreen

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.R
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.StartScreen.SplashScreen
import com.project.odoo_235.ui.theme.AppColors
import com.project.odoo_235.ui.theme.AppColors1
import com.project.odoo_235.ui.theme.Odoo_235Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("User") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loading by authViewModel.loading
    val signupSuccess by authViewModel.successSignup
    val signupMessage by authViewModel.signUpMessage

    LaunchedEffect(signupSuccess) {
        if (signupSuccess) {
            // After signup, prompt user to verify email, go to Login
            navController.popBackStack()
            navController.navigate("ResendVerification?email=${email}")
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
            Text(
                text = "Sign - Up",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = AppColors1.SportsDark
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
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
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Call,
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
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
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

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (password != confirmPassword) {
                        // show snack/toast outside
                        return@Button
                    }
                    authViewModel.signupJson(name, email, password, confirmPassword, role)
                },
                enabled = !loading && name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
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
                    if (loading) "Creating..." else "Create Account",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            if (!signupMessage.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(signupMessage!!, color = AppColors.OnSurfaceVariant)
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
                        painter = painterResource(id = com.project.odoo_235.R.drawable.google),
                        contentDescription = "Google",
                        tint = Color.Unspecified // keeps original logo colors
                    )
                }

                IconButton(onClick = { /* Facebook login */ }) {
                    Icon(
                        painter = painterResource(id = com.project.odoo_235.R.drawable.facebook),
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
                Text(
                    "Already have an account?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )


                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = AppColors1.SportsDark, // your #00BE76 green
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Login.routes)
                    }
                )
            }

        }

    }


}