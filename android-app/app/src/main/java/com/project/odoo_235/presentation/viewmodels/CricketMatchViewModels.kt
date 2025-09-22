// presentation/viewmodels/CricketMatchViewModels.kt
package com.project.odoo_235.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.odoo_235.data.api.CricketApiClient
import com.project.odoo_235.data.models.*
import com.project.odoo_235.data.repository.CricketWebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CricketMatchListViewModel : ViewModel() {
    private val apiClient = CricketApiClient()

    private val _matches = MutableStateFlow<List<CricketMatch>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            apiClient.getAllCricketMatches()
                .onSuccess { _matches.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun createCricketMatch(teamA: String, teamB: String, matchType: String = "T20", overs: Int = 20, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            apiClient.createCricketMatch(teamA, teamB, matchType, overs)
                .onSuccess { match ->
                    // Automatically set the creator as admin
                    apiClient.setMatchAdmin(match.id, "admin_${System.currentTimeMillis()}")
                    loadMatches() // Refresh the list
                    onSuccess(match.id)
                }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        apiClient.close()
    }
}

class CricketMatchDetailViewModel(private val matchId: String) : ViewModel() {
    private var webSocketManager: CricketWebSocketManager? = null
    private val apiClient = CricketApiClient()

    private val _match = MutableStateFlow<CricketMatch?>(null)
    val match = _match.asStateFlow()

    private val _connectionState = MutableStateFlow(CricketWebSocketManager.ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    init {
        connectWebSocket()
        loadMatchFromApi()
    }

    private fun connectWebSocket() {
        webSocketManager = CricketWebSocketManager(matchId).also { wsManager ->
            viewModelScope.launch {
                // Observe connection state
                wsManager.connectionState.collect { state ->
                    _connectionState.value = state
                }
            }

            viewModelScope.launch {
                // Observe messages
                wsManager.messages.collect { message ->
                    when (message) {
                        is CricketServerSnapshot -> _match.value = message.match
                        is CricketServerUpdate -> _match.value = message.match
                        is ServerSnapshot -> {
                            // Convert regular Match to CricketMatch for compatibility
                            val regularMatch = message.match
                            val cricketMatch = CricketMatch(
                                id = regularMatch.id,
                                teamA = regularMatch.teamA,
                                teamB = regularMatch.teamB,
                                matchType = regularMatch.matchType,
                                totalOvers = regularMatch.totalOvers,
                                status = regularMatch.status,
                                adminId = regularMatch.adminId,
                                createdAt = regularMatch.createdAt,
                                updatedAt = regularMatch.updatedAt,
                                innings = regularMatch.innings,
                                currentInnings = regularMatch.currentInnings,
                                currentOver = regularMatch.currentOver,
                                currentBall = regularMatch.currentBall,
                                currentBatsman = regularMatch.currentBatsman,
                                currentBowler = regularMatch.currentBowler,
                                target = regularMatch.target,
                                requiredRuns = regularMatch.requiredRuns,
                                requiredBalls = regularMatch.requiredBalls,
                                runRate = regularMatch.runRate,
                                requiredRunRate = regularMatch.requiredRunRate
                            )
                            _match.value = cricketMatch
                        }
                        is ServerUpdate -> {
                            // Convert regular Match to CricketMatch for compatibility
                            val regularMatch = message.match
                            val cricketMatch = CricketMatch(
                                id = regularMatch.id,
                                teamA = regularMatch.teamA,
                                teamB = regularMatch.teamB,
                                matchType = regularMatch.matchType,
                                totalOvers = regularMatch.totalOvers,
                                status = regularMatch.status,
                                adminId = regularMatch.adminId,
                                createdAt = regularMatch.createdAt,
                                updatedAt = regularMatch.updatedAt,
                                innings = regularMatch.innings,
                                currentInnings = regularMatch.currentInnings,
                                currentOver = regularMatch.currentOver,
                                currentBall = regularMatch.currentBall,
                                currentBatsman = regularMatch.currentBatsman,
                                currentBowler = regularMatch.currentBowler,
                                target = regularMatch.target,
                                requiredRuns = regularMatch.requiredRuns,
                                requiredBalls = regularMatch.requiredBalls,
                                runRate = regularMatch.runRate,
                                requiredRunRate = regularMatch.requiredRunRate
                            )
                            _match.value = cricketMatch
                        }
                        is ServerError -> _error.value = "${message.code}: ${message.message}"
                    }
                }
            }

            viewModelScope.launch {
                try {
                    wsManager.connect()
                } catch (e: Exception) {
                    _error.value = "Connection failed: ${e.message}"
                }
            }
        }
    }

    fun updateCricketScore(scoreUpdate: CricketScorePayload) {
        viewModelScope.launch {
            webSocketManager?.sendCricketScoreUpdate(scoreUpdate)
        }
    }
    
    fun addRun(runs: Int) {
        val currentMatch = _match.value
        if (currentMatch != null && currentMatch.innings.isNotEmpty()) {
            val currentInnings = currentMatch.innings.last()
            val newRuns = currentInnings.totalRuns + runs
            val newOvers = currentInnings.totalOvers + 0.1 // Add 0.1 over (1 legal ball)
            val newRunRate = if (newOvers > 0) newRuns / newOvers else 0.0
            
            // Check if over is complete (6 legal balls = 1 over)
            val ballsInOver = (newOvers * 10) % 10
            val finalOvers = if (ballsInOver >= 6) {
                // Over complete, move to next over
                (newOvers.toInt() + 1).toDouble()
            } else {
                newOvers
            }
            
            // Check if innings is complete (reached over limit)
            if (finalOvers >= currentMatch.totalOvers) {
                // Innings complete - create new innings
                createNewInnings(currentMatch, currentInnings, newRuns, finalOvers)
            } else {
                val updatedScore = CricketScorePayload(
                    runs = newRuns,
                    wickets = currentInnings.totalWickets,
                    overs = finalOvers,
                    runRate = if (finalOvers > 0) newRuns / finalOvers else 0.0
                )
                updateCricketScore(updatedScore)
            }
        }
    }
    
    fun addSpecialBall(ballType: String) {
        val currentMatch = _match.value
        if (currentMatch != null && currentMatch.innings.isNotEmpty()) {
            val currentInnings = currentMatch.innings.last()
            
            when (ballType) {
                "Wide" -> {
                    // Wide: +1 run, no ball count (re-bowl)
                    val newRuns = currentInnings.totalRuns + 1
                    val updatedScore = CricketScorePayload(
                        runs = newRuns,
                        wickets = currentInnings.totalWickets,
                        overs = currentInnings.totalOvers, // No ball count for wide
                        runRate = if (currentInnings.totalOvers > 0) newRuns / currentInnings.totalOvers else 0.0
                    )
                    updateCricketScore(updatedScore)
                }
                "No Ball" -> {
                    // No Ball: +1 run, no ball count (re-bowl), next ball is free hit
                    val newRuns = currentInnings.totalRuns + 1
                    val updatedScore = CricketScorePayload(
                        runs = newRuns,
                        wickets = currentInnings.totalWickets,
                        overs = currentInnings.totalOvers, // No ball count for no ball
                        runRate = if (currentInnings.totalOvers > 0) newRuns / currentInnings.totalOvers else 0.0
                    )
                    updateCricketScore(updatedScore)
                    // TODO: Set free hit flag for next ball
                }
                "Wicket" -> {
                    // Wicket: +1 wicket, +1 legal ball (only if wickets < 10)
                    if (currentInnings.totalWickets < 10) {
                        val newWickets = currentInnings.totalWickets + 1
                        val newOvers = currentInnings.totalOvers + 0.1
                        
                        // Check if over is complete (6 legal balls = 1 over)
                        val ballsInOver = (newOvers * 10) % 10
                        val finalOvers = if (ballsInOver >= 6) {
                            (newOvers.toInt() + 1).toDouble()
                        } else {
                            newOvers
                        }
                        
                        // Check if innings is complete (reached over limit or all out)
                        if (finalOvers >= currentMatch.totalOvers || newWickets >= 10) {
                            // Innings complete - create new innings
                            createNewInnings(currentMatch, currentInnings, currentInnings.totalRuns, finalOvers)
                        } else {
                            val updatedScore = CricketScorePayload(
                                runs = currentInnings.totalRuns,
                                wickets = newWickets,
                                overs = finalOvers,
                                runRate = if (finalOvers > 0) currentInnings.totalRuns / finalOvers else 0.0
                            )
                            updateCricketScore(updatedScore)
                        }
                    }
                }
            }
        }
    }

    private fun createNewInnings(
        currentMatch: CricketMatch,
        completedInnings: CricketInnings,
        finalRuns: Int,
        finalOvers: Double
    ) {
        viewModelScope.launch {
            // Complete the current innings
            val completedInningsUpdated = completedInnings.copy(
                totalRuns = finalRuns,
                totalOvers = finalOvers,
                runRate = if (finalOvers > 0) finalRuns / finalOvers else 0.0,
                isCompleted = true
            )
            
            // Create new innings (swap batting and bowling teams)
            val newInningsNumber = currentMatch.innings.size + 1
            val newInnings = CricketInnings(
                inningsNumber = newInningsNumber,
                battingTeam = completedInnings.bowlingTeam, // Swap teams
                bowlingTeam = completedInnings.battingTeam,
                totalRuns = 0,
                totalWickets = 0,
                totalOvers = 0.0,
                runRate = 0.0,
                batsmen = emptyList(),
                bowlers = emptyList(),
                overs = emptyList(),
                extras = Extras(),
                isCompleted = false
            )
            
            // Update the innings list
            val updatedInnings = currentMatch.innings.toMutableList()
            updatedInnings[updatedInnings.size - 1] = completedInningsUpdated
            updatedInnings.add(newInnings)
            
            // Update the match with new innings
            val updatedMatch = currentMatch.copy(
                innings = updatedInnings,
                currentInnings = newInningsNumber,
                target = finalRuns + 1, // Set target for second innings
                requiredRuns = finalRuns + 1,
                requiredBalls = currentMatch.totalOvers * 6, // Total balls available
                updatedAt = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            
            // Update local state
            _match.value = updatedMatch
            
            // Send update to backend
            val scoreUpdate = CricketScorePayload(
                runs = 0, // New innings starts with 0 runs
                wickets = 0,
                overs = 0.0,
                runRate = 0.0
            )
            updateCricketScore(scoreUpdate)
        }
    }

    fun updateScore(scoreA: Int, scoreB: Int) {
        viewModelScope.launch {
            webSocketManager?.sendScoreUpdate(scoreA, scoreB)
        }
    }

    fun updateInnings(innings: CricketInnings) {
        viewModelScope.launch {
            webSocketManager?.updateInnings(innings)
        }
    }

    fun updateBowler(bowler: Bowler) {
        viewModelScope.launch {
            webSocketManager?.updateBowler(bowler)
        }
    }

    fun updateBatsman(batsman: Batsman) {
        viewModelScope.launch {
            webSocketManager?.updateBatsman(batsman)
        }
    }

    fun refreshMatch() {
        viewModelScope.launch {
            webSocketManager?.requestSnapshot()
        }
    }

    fun setAdminStatus(isAdmin: Boolean) {
        _isAdmin.value = isAdmin
        if (isAdmin) {
            // Set admin via API - use device ID or timestamp as admin ID
            viewModelScope.launch {
                val adminId = "admin_${System.currentTimeMillis()}"
                apiClient.setMatchAdmin(matchId, adminId)
                    .onSuccess { updatedMatch ->
                        _match.value = updatedMatch
                    }
                    .onFailure { error ->
                        _error.value = "Failed to set admin: ${error.message}"
                    }
            }
        }
    }
    
    fun isMatchCreator(): Boolean {
        // Check if current user is the match creator
        // For now, we'll assume the first person to become admin is the creator
        return _isAdmin.value
    }

    fun updateMatchStatus(status: MatchStatus) {
        viewModelScope.launch {
            apiClient.updateMatchStatus(matchId, status)
                .onSuccess { updatedMatch ->
                    _match.value = updatedMatch
                    
                    // If starting the match, create initial innings
                    if (status == MatchStatus.IN_PROGRESS && updatedMatch.innings.isEmpty()) {
                        val initialInnings = CricketInnings(
                            inningsNumber = 1,
                            battingTeam = updatedMatch.teamA,
                            bowlingTeam = updatedMatch.teamB,
                            totalRuns = 0,
                            totalWickets = 0,
                            totalOvers = 0.0,
                            runRate = 0.0,
                            batsmen = emptyList(),
                            bowlers = emptyList(),
                            overs = emptyList(),
                            extras = Extras(),
                            isCompleted = false
                        )
                        updateInnings(initialInnings)
                    }
                }
                .onFailure { error ->
                    _error.value = "Failed to update status: ${error.message}"
                }
        }
    }

    private fun loadMatchFromApi() {
        viewModelScope.launch {
            apiClient.getCricketMatch(matchId)
                .onSuccess { match ->
                    _match.value = match
                }
                .onFailure { error ->
                    _error.value = "Failed to load match: ${error.message}"
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager?.close()
        apiClient.close()
    }
}
