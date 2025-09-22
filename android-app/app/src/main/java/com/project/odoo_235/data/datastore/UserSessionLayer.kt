// app/src/main/java/.../data/datastore/UserSessionManager.kt
package com.project.odoo_235.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("user_session")

private val json by lazy {
    Json { ignoreUnknownKeys = true; encodeDefaults = true }
}

@Serializable
data class CachedUser(
    val id: String,
    val name: String,
    val email: String,
    val role: String = "User",
    val isVerified: Boolean = false
)

class UserSessionManager(private val context: Context) {
    companion object {
        private val USER_JSON = stringPreferencesKey("user_json")
        private val JWT_TOKEN = stringPreferencesKey("jwt_token")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userData: Flow<CachedUser?> = context.dataStore.data.map { pref ->
        pref[USER_JSON]?.let { runCatching { json.decodeFromString(CachedUser.serializer(), it) }.getOrNull() }
    }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val jwtToken: Flow<String?> = context.dataStore.data.map { it[JWT_TOKEN] }

    suspend fun saveUserSession(id: String, name: String, email: String, role: String = "User", isVerified: Boolean = false, token: String) {
        val cached = CachedUser(id, name, email, role, isVerified)
        val payload = json.encodeToString(CachedUser.serializer(), cached)
        context.dataStore.edit { pref ->
            pref[USER_JSON] = payload
            pref[JWT_TOKEN] = token
            pref[IS_LOGGED_IN] = true
        }
    }



    suspend fun saveUser(id: String, name: String, email: String, role: String = "User", isVerified: Boolean = false) {
        val cached = CachedUser(id, name, email, role, isVerified)
        val payload = json.encodeToString(CachedUser.serializer(), cached)
        context.dataStore.edit { it[USER_JSON] = payload }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[JWT_TOKEN] = token; it[IS_LOGGED_IN] = true }
    }
    suspend fun getToken(): String? = context.dataStore.data.map { it[JWT_TOKEN] }.first()
    suspend fun getUser(): CachedUser? = context.dataStore.data.map { pref ->
        pref[USER_JSON]?.let { runCatching { json.decodeFromString(CachedUser.serializer(), it) }.getOrNull() }
    }.first()
    suspend fun checkIsLoggedIn(): Boolean = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }.first()
    suspend fun updateUser(updated: CachedUser) {
        val payload = json.encodeToString(CachedUser.serializer(), updated)
        context.dataStore.edit { it[USER_JSON] = payload }
    }
    suspend fun clearUser() { context.dataStore.edit { it.clear() } }
    suspend fun clearToken() { context.dataStore.edit { it.remove(JWT_TOKEN); it[IS_LOGGED_IN] = false } }
}