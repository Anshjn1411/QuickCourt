// data/api/CricketApiClient.kt
package com.project.odoo_235.data.api

import com.project.odoo_235.data.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

class CricketApiClient {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private val baseUrl = "http://192.168.43.29:8080"

    suspend fun createCricketMatch(teamA: String, teamB: String, matchType: String = "T20", overs: Int = 20): Result<CricketMatch> = try {
        val response = client.post("$baseUrl/matches") {
            contentType(ContentType.Application.Json)
            setBody(CreateCricketMatchRequest(teamA, teamB, matchType, overs))
        }
        Result.success(response.body<CricketMatch>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllCricketMatches(): Result<List<CricketMatch>> = try {
        val response = client.get("$baseUrl/matches")
        Result.success(response.body<List<CricketMatch>>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getCricketMatch(id: String): Result<CricketMatch> = try {
        val response = client.get("$baseUrl/matches/$id")
        Result.success(response.body<CricketMatch>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateMatchStatus(id: String, status: MatchStatus): Result<CricketMatch> = try {
        val response = client.put("$baseUrl/matches/$id/status") {
            contentType(ContentType.Application.Json)
            setBody(UpdateMatchStatusRequest(status))
        }
        Result.success(response.body<CricketMatch>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun setMatchAdmin(id: String, adminId: String): Result<CricketMatch> = try {
        val response = client.put("$baseUrl/matches/$id/admin") {
            contentType(ContentType.Application.Json)
            setBody(SetAdminRequest(adminId))
        }
        Result.success(response.body<CricketMatch>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun close() {
        client.close()
    }
}
