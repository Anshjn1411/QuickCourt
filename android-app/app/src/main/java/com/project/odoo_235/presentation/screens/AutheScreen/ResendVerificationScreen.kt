package com.project.odoo_235.presentation.screens.AutheScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResendVerificationScreen(authViewModel: AuthViewModel, prefillEmail: String? = null, onDone: () -> Unit) {
    var email by remember { mutableStateOf(prefillEmail.orEmpty()) }
    val loading by authViewModel.loading
    var message by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Verify your email", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = { authViewModel.resendVerification(email) { ok, msg -> message = msg; if (ok) onDone() } }, enabled = !loading && email.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text(if (loading) "Sending..." else "Send Verification Email")
        }
        if (!message.isNullOrBlank()) { Spacer(Modifier.height(8.dp)); Text(message!!) }
    }
}



@Composable
fun VerifyEmailScreen(token: String, authViewModel: AuthViewModel, onBackToLogin: () -> Unit) {
    val loading by authViewModel.loading
    var message by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(token) { authViewModel.verifyEmail(token) { ok, msg -> success = ok; message = msg } }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text(if (success == true) "Email Verified" else "Verification", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        if (loading) CircularProgressIndicator() else Text(message ?: if (success == true) "Your email is verified." else "Verification failed.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) { Text("Back to Login") }
    }
}



@Composable
fun ForgotPasswordScreen(authViewModel: AuthViewModel, onSent: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    val loading by authViewModel.loading
    var message by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Forgot Password", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = { authViewModel.forgotPassword(email) { ok, msg -> message = msg; if (ok) onSent(email) } }, enabled = !loading && email.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text(if (loading) "Sending..." else "Send Reset Link")
        }
        if (!message.isNullOrBlank()) { Spacer(Modifier.height(8.dp)); Text(message!!) }
    }
}

@Composable
fun ResetPasswordScreen(token: String, authViewModel: AuthViewModel, onResetDone: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val loading by authViewModel.loading
    var message by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Reset Password", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("New Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirm Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (password != confirm) { message = "Passwords do not match"; return@Button }
            authViewModel.resetPassword(token, password, confirm) { ok, msg -> message = msg; if (ok) onResetDone() }
        }, enabled = !loading && password.isNotBlank() && confirm.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text(if (loading) "Updating..." else "Update Password")
        }
        if (!message.isNullOrBlank()) { Spacer(Modifier.height(8.dp)); Text(message!!) }
    }
}


@Composable
fun UpdatePasswordScreen(authViewModel: AuthViewModel, onUpdated: () -> Unit) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val loading by authViewModel.loading
    var message by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Change Password", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it }, label = { Text("Current Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("New Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirm New Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (newPassword != confirm) { message = "Passwords do not match"; return@Button }
            authViewModel.updatePassword(oldPassword, newPassword, confirm) { ok, msg -> message = msg; if (ok) onUpdated() }
        }, enabled = !loading && oldPassword.isNotBlank() && newPassword.isNotBlank() && confirm.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
            Text(if (loading) "Saving..." else "Save Password")
        }
        if (!message.isNullOrBlank()) { Spacer(Modifier.height(8.dp)); Text(message!!) }
    }
}