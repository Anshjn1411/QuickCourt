// presentation/viewmodels/MatchViewModels.kt
package com.project.odoo_235.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.odoo_235.data.api.MatchApiClient
import com.project.odoo_235.data.models.Batsman
import com.project.odoo_235.data.models.Bowler
import com.project.odoo_235.data.models.CricketInnings
import com.project.odoo_235.data.models.CricketScorePayload
import com.project.odoo_235.data.models.CricketServerSnapshot
import com.project.odoo_235.data.models.CricketServerUpdate
import com.project.odoo_235.data.models.Match
import com.project.odoo_235.data.models.ServerError
import com.project.odoo_235.data.models.ServerSnapshot
import com.project.odoo_235.data.models.ServerUpdate
import com.project.odoo_235.data.repository.WebSocketManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchListViewModel : ViewModel() {
    private val apiClient = MatchApiClient()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
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

            apiClient.getAllMatches()
                .onSuccess { _matches.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun createMatch(teamA: String, teamB: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            apiClient.createMatch(teamA, teamB)
                .onSuccess { match ->
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

class MatchDetailViewModel(private val matchId: String) : ViewModel() {
    private var webSocketManager: WebSocketManager? = null

    private val _match = MutableStateFlow<Match?>(null)
    val match = _match.asStateFlow()

    private val _connectionState = MutableStateFlow(WebSocketManager.ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        connectWebSocket()
    }

    private fun connectWebSocket() {
        webSocketManager = WebSocketManager(matchId).also { wsManager ->
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
                        is ServerSnapshot -> _match.value = message.match
                        is ServerUpdate -> _match.value = message.match
                        is CricketServerSnapshot -> {
                            // Convert CricketMatch to Match for compatibility
                            val cricketMatch = message.match
                            val regularMatch = Match(
                                id = cricketMatch.id,
                                teamA = cricketMatch.teamA,
                                teamB = cricketMatch.teamB,

                                matchType = cricketMatch.matchType,
                                totalOvers = cricketMatch.totalOvers,
                                scoreA = cricketMatch.innings.lastOrNull()?.totalRuns ?: 0,
                                scoreB = 0, // This would need to be calculated based on innings,
                                createdAt = cricketMatch.createdAt,
                                updatedAt = cricketMatch.updatedAt
                            )
                            _match.value = regularMatch
                        }
                        is CricketServerUpdate -> {
                            // Convert CricketMatch to Match for compatibility
                            val cricketMatch = message.match
                            val regularMatch = Match(
                                id = cricketMatch.id,
                                teamA = cricketMatch.teamA,
                                teamB = cricketMatch.teamB,
                                matchType = cricketMatch.matchType,
                                totalOvers = cricketMatch.totalOvers,
                                scoreA = cricketMatch.innings.lastOrNull()?.totalRuns ?: 0,
                                scoreB = 0 ,// This would need to be calculated based on innings
                                createdAt = cricketMatch.createdAt,
                                updatedAt = cricketMatch.updatedAt
                            )
                            _match.value = regularMatch
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

    fun updateScore(scoreA: Int, scoreB: Int) {
        viewModelScope.launch {
            webSocketManager?.sendScoreUpdate(scoreA, scoreB)
        }
    }

    fun updateCricketScore(scoreUpdate: CricketScorePayload) {
        viewModelScope.launch {
            webSocketManager?.sendCricketScoreUpdate(scoreUpdate)
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

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager?.close()
    }
}