// presentation/screens/livematches/CricketAdminPanelNew.kt
package com.project.odoo_235.presentation.screens.user.screen.livematches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.odoo_235.data.models.*
import com.project.odoo_235.presentation.viewmodels.CricketMatchDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketAdminPanelNew(
    matchId: String,
    onNavigateBack: () -> Unit
) {
    val viewModel: CricketMatchDetailViewModel = viewModel { CricketMatchDetailViewModel(matchId) }
    val match by viewModel.match.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    // Auto-set admin for match creator
    LaunchedEffect(Unit) {
        viewModel.setAdminStatus(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "🏏 Cricket Admin",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color(0xFF2E7D32))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F5E8)
                )
            )
        }
    ) { paddingValues ->
        match?.let { currentMatch ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE8F5E8),
                                Color(0xFFF1F8E9)
                            )
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    CricketMatchHeader(match = currentMatch)
                }

                item {
                    MatchStatusCard(match = currentMatch, onStatusUpdate = viewModel::updateMatchStatus)
                }

                if (currentMatch.innings.isNotEmpty()) {
                    item {
                        CurrentScoreCard(
                            innings = currentMatch.innings.last(),
                            onScoreUpdate = viewModel::updateCricketScore
                        )
                    }
                    
                    // Show innings history if multiple innings
                    if (currentMatch.innings.size > 1) {
                        item {
                            CricketInningsHistoryCard(
                                innings = currentMatch.innings
                            )
                        }
                    }
                }

                item {
                    QuickRunButtons(
                        onRunScored = { runs ->
                            viewModel.addRun(runs)
                        }
                    )
                }

                item {
                    SpecialBallButtons(
                        onSpecialBall = { ballType ->
                            viewModel.addSpecialBall(ballType)
                        },
                        currentWickets = currentMatch.innings.lastOrNull()?.totalWickets ?: 0
                    )
                }

                item {
                    PlayerManagementCard(
                        match = currentMatch,
                        onBatsmanUpdate = viewModel::updateBatsman,
                        onBowlerUpdate = viewModel::updateBowler
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
fun CricketMatchHeader(match: CricketMatch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E7D32)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏏 CRICKET MATCH 🏏",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "${match.teamA} vs ${match.teamB}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${match.matchType} • ${match.totalOvers} overs",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun MatchStatusCard(
    match: CricketMatch,
    onStatusUpdate: (MatchStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (match.status) {
                MatchStatus.SCHEDULED -> Color(0xFF2196F3)
                MatchStatus.IN_PROGRESS -> Color(0xFF4CAF50)
                MatchStatus.COMPLETED -> Color(0xFF9E9E9E)
                else -> Color(0xFFFF9800)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Match Status",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = match.status.name.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (match.status == MatchStatus.SCHEDULED) {
                Button(
                    onClick = { onStatusUpdate(MatchStatus.IN_PROGRESS) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Match", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CurrentScoreCard(
    innings: CricketInnings,
    onScoreUpdate: (CricketScorePayload) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current Score",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Score Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${innings.totalRuns}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Runs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Text(
                    text = "/",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${innings.totalWickets}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                    Text(
                        text = "Wickets",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Overs and Run Rate
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", innings.totalOvers),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Overs",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.2f", innings.runRate),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Run Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun QuickRunButtons(
    onRunScored: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🏏 Quick Runs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0, 1, 2, 3, 4, 6).forEach { runs ->
                    val buttonColor = when (runs) {
                        0 -> Color(0xFF9E9E9E)
                        1 -> Color(0xFF2196F3)
                        2 -> Color(0xFF4CAF50)
                        3 -> Color(0xFFFF9800)
                        4 -> Color(0xFFE91E63)
                        6 -> Color(0xFF9C27B0)
                        else -> Color(0xFF2E7D32)
                    }
                    
                    var isPressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.95f else 1f,
                        animationSpec = tween(100)
                    )
                    
                    Button(
                        onClick = {
                            onRunScored(runs)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = Color.White
                        ),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "$runs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialBallButtons(
    onSpecialBall: (String) -> Unit,
    currentWickets: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎯 Special Balls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Wide" to Color(0xFFFF5722),
                    "No Ball" to Color(0xFFE91E63),
                    "Wicket" to Color(0xFFD32F2F)
                ).forEach { (ballType, color) ->
                    val isWicketDisabled = ballType == "Wicket" && currentWickets >= 10
                    
                    Button(
                        onClick = { 
                            if (!isWicketDisabled) {
                                onSpecialBall(ballType)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isWicketDisabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWicketDisabled) Color.Gray else color,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isWicketDisabled) "All Out" else ballType,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun PlayerManagementCard(
    match: CricketMatch,
    onBatsmanUpdate: (Batsman) -> Unit,
    onBowlerUpdate: (Bowler) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "👥 Player Management",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        onBatsmanUpdate(
                            Batsman(
                                name = "New Batsman",
                                isOnStrike = true
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Batsman")
                }
                
                Button(
                    onClick = { 
                        onBowlerUpdate(
                            Bowler(
                                name = "New Bowler",
                                isBowling = true
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Bowler")
                }
            }
        }
    }
}
