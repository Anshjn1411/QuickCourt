// data/api/MatchApiClient.kt
package com.project.odoo_235.data.api

import com.project.odoo_235.data.models.CreateMatchRequest
import com.project.odoo_235.data.models.Match
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

class MatchApiClient {
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
        // Remove WebSockets from here - we'll handle it separately
    }

    private val baseUrl = "http://192.168.43.29:8080"

    suspend fun createMatch(teamA: String, teamB: String): Result<Match> = try {
        val response = client.post("$baseUrl/matches") {
            contentType(ContentType.Application.Json)
            setBody(CreateMatchRequest(teamA, teamB))
        }
        Result.success(response.body<Match>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getAllMatches(): Result<List<Match>> = try {
        val response = client.get("$baseUrl/matches")
        Result.success(response.body<List<Match>>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getMatch(id: String): Result<Match> = try {
        val response = client.get("$baseUrl/matches/$id")
        Result.success(response.body<Match>())
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun close() {
        client.close()
    }
}