// data/repository/CricketWebSocketManager.kt
package com.project.odoo_235.data.repository

import android.util.Log
import kotlinx.serialization.*
import com.project.odoo_235.data.models.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class CricketWebSocketManager(private val matchId: String) {
    private val client = HttpClient(OkHttp) {
        install(WebSockets)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    private val _messages = MutableSharedFlow<ServerMessage>()
    val messages = _messages.asSharedFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    enum class ConnectionState { CONNECTING, CONNECTED, DISCONNECTED, ERROR }

    suspend fun connect() {
        try {
            _connectionState.value = ConnectionState.CONNECTING
            client.webSocket(
                method = HttpMethod.Get,
                host = "192.168.43.29",
                port = 8080,
                path = "/ws/$matchId"
            ) {
                session = this
                _connectionState.value = ConnectionState.CONNECTED
                Log.d("CricketWebSocket", "Connected to match $matchId")

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            Log.d("CricketWebSocket", "Received: $text")
                            try {
                                val message = Json.decodeFromString<ServerMessage>(text)
                                _messages.emit(message)
                            } catch (e: Exception) {
                                Log.e("CricketWebSocket", "Failed to parse message: $text", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CricketWebSocket", "Error in message loop", e)
                } finally {
                    _connectionState.value = ConnectionState.DISCONNECTED
                }
            }
        } catch (e: Exception) {
            Log.e("CricketWebSocket", "Connection failed", e)
            _connectionState.value = ConnectionState.ERROR
        }
    }

    suspend fun sendCricketScoreUpdate(scoreUpdate: CricketScorePayload) {
        session?.let { s ->
            try {
                val message = ClientMessage.ScoreUpdate(scoreUpdate)
                val json = Json.encodeToString<ClientMessage>(message)
                Log.d("CricketWebSocket", "Sending cricket score update: $json")
                Log.d("CricketWebSocket", "Score data - runs: ${scoreUpdate.runs}, wickets: ${scoreUpdate.wickets}, overs: ${scoreUpdate.overs}")
                s.send(json)
            } catch (e: Exception) {
                Log.e("CricketWebSocket", "Failed to send cricket score update", e)
            }
        }
    }

    suspend fun sendScoreUpdate(scoreA: Int, scoreB: Int) {
        session?.let { s ->
            try {
                val scoreUpdate = CricketScorePayload(
                    runs = scoreA,
                    wickets = 0,
                    overs = 0.0,
                    runRate = 0.0
                )
                val message = ClientMessage.ScoreUpdate(scoreUpdate)
                val json = Json.encodeToString<ClientMessage>(message)
                Log.d("CricketWebSocket", "Sending score update: $json")
                s.send(json)
            } catch (e: Exception) {
                Log.e("CricketWebSocket", "Failed to send score update", e)
            }
        }
    }

    suspend fun updateInnings(innings: CricketInnings) {
        session?.let { s ->
            try {
                val message = ClientMessage.UpdateInnings(innings)
                val json = Json.encodeToString<ClientMessage>(message)
                s.send(json)
            } catch (e: Exception) {
                Log.e("CricketWebSocket", "Failed to update innings", e)
            }
        }
    }

    suspend fun updateBowler(bowler: Bowler) {
        session?.let { s ->
            try {
                val message = ClientMessage.UpdateBowler(bowler)
                val json = Json.encodeToString<ClientMessage>(message)
                s.send(json)
            } catch (e: Exception) {
                Log.e("CricketWebSocket", "Failed to update bowler", e)
            }
        }
    }

    suspend fun updateBatsman(batsman: Batsman) {
        session?.let { s ->
            try {
                val message = ClientMessage.UpdateBatsman(batsman)
                val json = Json.encodeToString<ClientMessage>(message)
                s.send(json)
            } catch (e: Exception) {
                Log.e("CricketWebSocket", "Failed to update batsman", e)
            }
        }
    }

    suspend fun requestSnapshot() {
        session?.let { s ->
            try {
                val message = ClientMessage.RequestSnapshot
                val json = Json.encodeToString<ClientMessage>(message)
                s.send(json)
            } catch (e: Exception) {
                Log.e("CricketWebSocket", "Failed to request snapshot", e)
            }
        }
    }

    fun disconnect() {
        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    fun close() {
        disconnect()
        client.close()
    }
}
