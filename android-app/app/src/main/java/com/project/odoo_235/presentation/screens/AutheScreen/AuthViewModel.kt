// app/src/main/java/.../presentation/auth/AuthViewModel.kt
package com.project.odoo_235.presentation.screens.AutheScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.odoo_235.data.api.RetrofitInstance
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.data.models.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

open class AuthViewModel(private val sessionManager: UserSessionManager) : ViewModel() {
    private val _loading = mutableStateOf(false)
    val loading = _loading

    private val _loginMessage = mutableStateOf<String?>(null)
    val loginMessage = _loginMessage

    private val _signUpMessage = mutableStateOf<String?>(null)
    val signUpMessage = _signUpMessage

    private val _successLogin = mutableStateOf(false)
    val successLogin = _successLogin

    private val _successSignup = mutableStateOf(false)
    val successSignup = _successSignup

    private val _role = mutableStateOf<String?>(null)
    val role = _role

    init { autoLogin() }

    private fun autoLogin() {
        viewModelScope.launch {
            runCatching { sessionManager.getUser() }
                .onSuccess { u -> if (u != null) { _successLogin.value = true; _role.value = u.role } }
        }
    }

    open fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            val api = RetrofitInstance.getApi(sessionManager)
            runCatching { api.login(LoginRequest(email, password)) }
                .onSuccess { response ->
                    if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                        val body = response.body()!!
                        val u = body.user
                        sessionManager.saveUserSession(u.id, u.name, u.email, u.role, u.isVerified ?: false, body.token)
                        _successLogin.value = true
                        _role.value = u.role
                        _loginMessage.value = "Welcome, ${u.name}"
                    } else {
                        val err = response.errorBody()?.string().orEmpty()
                        _loginMessage.value = if (err.isNotBlank()) err else response.message()
                        _successLogin.value = false
                    }
                }.onFailure { e ->
                    _loginMessage.value = e.localizedMessage
                    _successLogin.value = false
                }
            _loading.value = false
        }
    }

    // Signup JSON (if no avatar)
    fun signupJson(name: String, email: String, password: String, confirmPassword: String, role: String) {
        viewModelScope.launch {
            _loading.value = true
            val api = RetrofitInstance.getApi(sessionManager)
            runCatching { api.signupJson(RegisterRequest(name, email, password, confirmPassword, role)) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) {
                        val u = resp.body()!!.user
                        _signUpMessage.value = "Account created. Please verify your email."
                        _successSignup.value = true
                    } else {
                        _signUpMessage.value = resp.errorBody()?.string() ?: resp.message()
                        _successSignup.value = false
                    }
                }.onFailure { e ->
                    _signUpMessage.value = e.localizedMessage
                    _successSignup.value = false
                }
            _loading.value = false
        }
    }

    // Signup Multipart (with avatar)
    fun signupMultipart(context: Context, name: String, email: String, password: String, confirmPassword: String, role: String, avatar: Uri?) {
        viewModelScope.launch {
            _loading.value = true
            val api = RetrofitInstance.getApi(sessionManager)
            val nameP = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val emailP = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val passP = RequestBody.create("text/plain".toMediaTypeOrNull(), password)
            val confP = RequestBody.create("text/plain".toMediaTypeOrNull(), confirmPassword)
            val roleP = RequestBody.create("text/plain".toMediaTypeOrNull(), role)
            val avatarPart = avatar?.let {
                val f = File(context.cacheDir, "avatar_${System.currentTimeMillis()}")
                context.contentResolver.openInputStream(it)?.use { input -> f.outputStream().use { o -> input.copyTo(o) } }
                val body = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), f)
                MultipartBody.Part.createFormData("avatar", f.name, body)
            }
            runCatching { api.signupMultipart(nameP, emailP, passP, confP, roleP, avatarPart) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) {
                        _signUpMessage.value = "Account created. Please verify your email."
                        _successSignup.value = true
                    } else {
                        _signUpMessage.value = resp.errorBody()?.string() ?: resp.message()
                        _successSignup.value = false
                    }
                }.onFailure { e ->
                    _signUpMessage.value = e.localizedMessage
                    _successSignup.value = false
                }
            _loading.value = false
        }
    }

    fun verifyEmail(token: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            runCatching { RetrofitInstance.getApi(sessionManager).verifyEmail(token) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) onDone(true, resp.body()!!.message)
                    else onDone(false, resp.errorBody()?.string() ?: resp.message())
                }.onFailure { onDone(false, it.localizedMessage ?: "Verification failed") }
            _loading.value = false
        }
    }

    fun resendVerification(email: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            runCatching { RetrofitInstance.getApi(sessionManager).resendVerification(ResendVerificationRequest(email)) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) onDone(true, "Verification email sent")
                    else onDone(false, resp.errorBody()?.string() ?: resp.message())
                }.onFailure { onDone(false, it.localizedMessage ?: "Failed to resend") }
            _loading.value = false
        }
    }

    fun forgotPassword(email: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            runCatching { RetrofitInstance.getApi(sessionManager).forgotPassword(ForgotPasswordRequest(email)) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) onDone(true, resp.body()!!.message)
                    else onDone(false, resp.errorBody()?.string() ?: resp.message())
                }.onFailure { onDone(false, it.localizedMessage ?: "Failed to request reset") }
            _loading.value = false
        }
    }

    fun resetPassword(token: String, password: String, confirm: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            runCatching { RetrofitInstance.getApi(sessionManager).resetPassword(token, ResetPasswordRequest(password, confirm)) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) {
                        val body = resp.body()!!
                        val u = body.user
                        sessionManager.saveUserSession(u.id, u.name, u.email, u.role, u.isVerified ?: false, body.token)
                        _successLogin.value = true
                        _role.value = u.role
                        onDone(true, "Password reset successful")
                    } else onDone(false, resp.errorBody()?.string() ?: resp.message())
                }.onFailure { onDone(false, it.localizedMessage ?: "Reset failed") }
            _loading.value = false
        }
    }

    fun updatePassword(oldPassword: String, newPassword: String, confirm: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            runCatching { RetrofitInstance.getApi(sessionManager).updatePassword(UpdatePasswordRequest(oldPassword, newPassword, confirm)) }
                .onSuccess { resp ->
                    if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) {
                        val body = resp.body()!!
                        val u = body.user
                        sessionManager.saveUserSession(u.id, u.name, u.email, u.role, u.isVerified ?: true, body.token)
                        onDone(true, "Password updated")
                    } else onDone(false, resp.errorBody()?.string() ?: resp.message())
                }.onFailure { onDone(false, it.localizedMessage ?: "Update failed") }
            _loading.value = false
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            runCatching { RetrofitInstance.getApi(sessionManager).logout() }
            sessionManager.clearUser()
            _successLogin.value = false
            _role.value = null
            onLoggedOut()
        }
    }
}