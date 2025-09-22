package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.Collections

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) { json() }

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(30)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        // REST: create a cricket match
        post("/matches") {
            val req = call.receive<CreateCricketMatchRequest>()
            val match = CricketMatchStore.createMatch(req.teamA, req.teamB, req.matchType, req.overs)
            call.respond(match)
        }

        // REST: list matches
        get("/matches") { call.respond(CricketMatchStore.getAll()) }

        // REST: get match by id
        get("/matches/{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("Missing id")
            val match = CricketMatchStore.get(id) ?: return@get call.respondText("Not found")
            call.respond(match)
        }

        // REST: update match status
        put("/matches/{id}/status") {
            val id = call.parameters["id"] ?: return@put call.respondText("Missing id")
            val req = call.receive<UpdateMatchStatusRequest>()
            val updated = CricketMatchStore.updateMatchStatus(id, req.status)
            if (updated != null) {
                CricketMatchStore.broadcast(id, ServerUpdate(updated))
                call.respond(updated)
            } else {
                call.respondText("Match not found", status = HttpStatusCode.NotFound)
            }
        }

        // REST: set admin for match
        put("/matches/{id}/admin") {
            val id = call.parameters["id"] ?: return@put call.respondText("Missing id")
            val req = call.receive<SetAdminRequest>()
            val updated = CricketMatchStore.setAdmin(id, req.adminId)
            if (updated != null) {
                CricketMatchStore.broadcast(id, ServerUpdate(updated))
                call.respond(updated)
            } else {
                call.respondText("Match not found", status = HttpStatusCode.NotFound)
            }
        }

        // WebSocket for live updates
        webSocket("/ws/{matchId}") {
            val matchId = call.parameters["matchId"] ?: return@webSocket close(
                CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing matchId")
            )

            val registered = CricketMatchStore.registerClient(matchId, this)
            if (!registered) {
                sendSerialized(ServerError("match_not_found", "Match with id $matchId not found"))
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Match not found"))
                return@webSocket
            }

            // send snapshot immediately
            CricketMatchStore.get(matchId)?.let { sendSerialized(ServerSnapshot(it)) }

            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val msg = Json.decodeFromString<ClientMessage>(text)
                            when (msg) {
                                is ClientMessage.Heartbeat -> {}
                                is ClientMessage.ScoreUpdate -> {
                                    println("Received score update: ${msg.update}")
                                    val updated = CricketMatchStore.updateScore(
                                        matchId, msg.update
                                    )
                                    updated?.let {
                                        println("Broadcasting updated match: ${it.innings}")
                                        CricketMatchStore.broadcast(matchId, ServerUpdate(it))
                                    }
                                }
                                is ClientMessage.RequestSnapshot -> {
                                    CricketMatchStore.get(matchId)?.let { sendSerialized(ServerSnapshot(it)) }
                                }
                                is ClientMessage.UpdateInnings -> {
                                    val updated = CricketMatchStore.updateInnings(
                                        matchId, msg.innings
                                    )
                                    updated?.let {
                                        CricketMatchStore.broadcast(matchId, ServerUpdate(it))
                                    }
                                }
                                is ClientMessage.UpdateBowler -> {
                                    val updated = CricketMatchStore.updateBowler(
                                        matchId, msg.bowler
                                    )
                                    updated?.let {
                                        CricketMatchStore.broadcast(matchId, ServerUpdate(it))
                                    }
                                }
                                is ClientMessage.UpdateBatsman -> {
                                    val updated = CricketMatchStore.updateBatsman(
                                        matchId, msg.batsman
                                    )
                                    updated?.let {
                                        CricketMatchStore.broadcast(matchId, ServerUpdate(it))
                                    }
                                }
                            }
                        } catch (t: Throwable) {
                            sendSerialized(ServerError("invalid_message", t.message ?: "bad payload"))
                        }
                    }
                }
            } finally {
                CricketMatchStore.unregisterClient(matchId, this)
            }
        }
    }
}

/* ---------- Enhanced Cricket Data Models ---------- */

@Serializable
data class CricketMatch(
    val id: String,
    val teamA: String,
    val teamB: String,
    val matchType: String, // "T20", "ODI", "Test"
    val totalOvers: Int,
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val adminId: String? = null,
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val updatedAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val innings: List<CricketInnings> = emptyList(),
    val currentInnings: Int = 1,
    val currentOver: Int = 0,
    val currentBall: Int = 0,
    val currentBatsman: String? = null,
    val currentBowler: String? = null,
    val target: Int? = null,
    val requiredRuns: Int? = null,
    val requiredBalls: Int? = null,
    val runRate: Double = 0.0,
    val requiredRunRate: Double? = null
)

@Serializable
data class CricketInnings(
    val inningsNumber: Int,
    val battingTeam: String,
    val bowlingTeam: String,
    val totalRuns: Int = 0,
    val totalWickets: Int = 0,
    val totalOvers: Double = 0.0,
    val runRate: Double = 0.0,
    val batsmen: List<Batsman> = emptyList(),
    val bowlers: List<Bowler> = emptyList(),
    val overs: List<Over> = emptyList(),
    val extras: Extras = Extras(),
    val isCompleted: Boolean = false
)

@Serializable
data class Batsman(
    val name: String,
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val strikeRate: Double = 0.0,
    val isOut: Boolean = false,
    val isOnStrike: Boolean = false
)

@Serializable
data class Bowler(
    val name: String,
    val overs: Double = 0.0,
    val maidens: Int = 0,
    val runs: Int = 0,
    val wickets: Int = 0,
    val economy: Double = 0.0,
    val isBowling: Boolean = false
)

@Serializable
data class Over(
    val overNumber: Int,
    val bowler: String,
    val balls: List<Ball> = emptyList(),
    val runs: Int = 0,
    val wickets: Int = 0,
    val extras: Int = 0
)

@Serializable
data class Ball(
    val ballNumber: Int,
    val runs: Int = 0,
    val isWide: Boolean = false,
    val isNoBall: Boolean = false,
    val isBye: Boolean = false,
    val isLegBye: Boolean = false,
    val isWicket: Boolean = false,
    val wicketType: String? = null,
    val batsman: String? = null
)

@Serializable
data class Extras(
    val wides: Int = 0,
    val noBalls: Int = 0,
    val byes: Int = 0,
    val legByes: Int = 0,
    val total: Int = 0
)

@Serializable
enum class MatchStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, ABANDONED
}

@Serializable
data class CreateCricketMatchRequest(
    val teamA: String,
    val teamB: String,
    val matchType: String = "T20",
    val overs: Int = 20
)

@Serializable
data class UpdateMatchStatusRequest(val status: MatchStatus)

@Serializable
data class SetAdminRequest(val adminId: String)

@Serializable
data class CricketScorePayload(
    val runs: Int,
    val wickets: Int,
    val overs: Double,
    val runRate: Double,
    val batsman: String? = null,
    val bowler: String? = null,
    val ball: Ball? = null
)

/* ---------- Enhanced Client Messages ---------- */
@Serializable
sealed class ClientMessage {
    @Serializable @SerialName("score_update")
    data class ScoreUpdate(val update: CricketScorePayload) : ClientMessage()

    @Serializable @SerialName("heartbeat")
    object Heartbeat : ClientMessage()

    @Serializable @SerialName("request_snapshot")
    object RequestSnapshot : ClientMessage()

    @Serializable @SerialName("update_innings")
    data class UpdateInnings(val innings: CricketInnings) : ClientMessage()

    @Serializable @SerialName("update_bowler")
    data class UpdateBowler(val bowler: Bowler) : ClientMessage()

    @Serializable @SerialName("update_batsman")
    data class UpdateBatsman(val batsman: Batsman) : ClientMessage()
}

/* ---------- Server Messages ---------- */
@Serializable sealed class ServerMessage
@Serializable data class ServerSnapshot(val match: CricketMatch) : ServerMessage()
@Serializable data class ServerUpdate(val match: CricketMatch) : ServerMessage()
@Serializable data class ServerError(val code: String, val message: String) : ServerMessage()

suspend fun DefaultWebSocketServerSession.sendSerialized(msg: ServerMessage) {
    send(Json.encodeToString(msg))
}

/* ---------- Enhanced Cricket Match Store ---------- */
object CricketMatchStore {
    private val matches = ConcurrentHashMap<String, CricketMatch>()
    private val clients = ConcurrentHashMap<String, MutableSet<DefaultWebSocketServerSession>>()
    private val mutexes = ConcurrentHashMap<String, Mutex>()

    fun createMatch(teamA: String, teamB: String, matchType: String, overs: Int): CricketMatch {
        val id = UUID.randomUUID().toString()
        val match = CricketMatch(
            id = id,
            teamA = teamA,
            teamB = teamB,
            matchType = matchType,
            totalOvers = overs,
            status = MatchStatus.SCHEDULED
        )
        matches[id] = match
        clients.putIfAbsent(id, Collections.synchronizedSet(mutableSetOf()))
        mutexes.putIfAbsent(id, Mutex())
        return match
    }

    fun getAll(): List<CricketMatch> = matches.values.toList()
    fun get(id: String): CricketMatch? = matches[id]

    fun registerClient(matchId: String, session: DefaultWebSocketServerSession): Boolean {
        val set = clients[matchId] ?: return false
        set.add(session)
        return true
    }

    fun unregisterClient(matchId: String, session: DefaultWebSocketServerSession) {
        clients[matchId]?.remove(session)
    }

    suspend fun updateScore(matchId: String, scoreUpdate: CricketScorePayload): CricketMatch? {
        val mtx = mutexes[matchId] ?: return null
        return mtx.withLock {
            val existing = matches[matchId] ?: return null
            
            // Update the current innings with the score data
            val updatedInnings = existing.innings.toMutableList()
            if (updatedInnings.isNotEmpty()) {
                val currentInningsIndex = updatedInnings.size - 1
                val currentInnings = updatedInnings[currentInningsIndex]
                val updatedCurrentInnings = currentInnings.copy(
                    totalRuns = scoreUpdate.runs,
                    totalWickets = scoreUpdate.wickets,
                    totalOvers = scoreUpdate.overs,
                    runRate = scoreUpdate.runRate
                )
                updatedInnings[currentInningsIndex] = updatedCurrentInnings
            }
            
            val updated = existing.copy(
                updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                runRate = scoreUpdate.runRate,
                innings = updatedInnings
            )
            matches[matchId] = updated
            updated
        }
    }

    suspend fun updateMatchStatus(matchId: String, status: MatchStatus): CricketMatch? {
        val mtx = mutexes[matchId] ?: return null
        return mtx.withLock {
            val existing = matches[matchId] ?: return null
            val updated = existing.copy(
                status = status,
                updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            matches[matchId] = updated
            updated
        }
    }

    suspend fun setAdmin(matchId: String, adminId: String): CricketMatch? {
        val mtx = mutexes[matchId] ?: return null
        return mtx.withLock {
            val existing = matches[matchId] ?: return null
            val updated = existing.copy(
                adminId = adminId,
                updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            matches[matchId] = updated
            updated
        }
    }

    suspend fun updateInnings(matchId: String, innings: CricketInnings): CricketMatch? {
        val mtx = mutexes[matchId] ?: return null
        return mtx.withLock {
            val existing = matches[matchId] ?: return null
            val updatedInnings = existing.innings.toMutableList()
            val index = updatedInnings.indexOfFirst { it.inningsNumber == innings.inningsNumber }
            if (index >= 0) {
                updatedInnings[index] = innings
            } else {
                updatedInnings.add(innings)
            }
            val updated = existing.copy(
                innings = updatedInnings,
                updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            matches[matchId] = updated
            updated
        }
    }

    suspend fun updateBowler(matchId: String, bowler: Bowler): CricketMatch? {
        val mtx = mutexes[matchId] ?: return null
        return mtx.withLock {
            val existing = matches[matchId] ?: return null
            val updated = existing.copy(
                currentBowler = bowler.name,
                updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            matches[matchId] = updated
            updated
        }
    }

    suspend fun updateBatsman(matchId: String, batsman: Batsman): CricketMatch? {
        val mtx = mutexes[matchId] ?: return null
        return mtx.withLock {
            val existing = matches[matchId] ?: return null
            val updated = existing.copy(
                currentBatsman = batsman.name,
                updatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            matches[matchId] = updated
            updated
        }
    }

    fun broadcast(matchId: String, message: ServerMessage) {
        val set = clients[matchId] ?: return
        val text = Json.encodeToString(message)
        for (s in set.toList()) {
            try {
                s.launch { s.send(text) }
            } catch (_: Throwable) {}
        }
    }
}
