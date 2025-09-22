// presentation/screens/livematches/CricketMatchScreens.kt
package com.project.odoo_235.presentation.screens.user.screen.livematches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.odoo_235.data.models.*
import com.project.odoo_235.data.repository.CricketWebSocketManager
import com.project.odoo_235.presentation.viewmodels.CricketMatchDetailViewModel
import com.project.odoo_235.presentation.viewmodels.CricketMatchListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketMatchListScreen(
    onNavigateToMatch: (String) -> Unit,
    onNavigateToAdmin: (String) -> Unit = {},
    viewModel: CricketMatchListViewModel = viewModel()
) {
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cricket Live Scores") },
                actions = {
                    IconButton(onClick = viewModel::loadMatches) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, "Create Match")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error display
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = viewModel::clearError
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            if (isLoading && matches.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(matches, key = { it.id }) { match ->
                        CricketMatchCard(
                            match = match,
                            onClick = { onNavigateToMatch(match.id) }
                        )
                    }
                }
            }

            if (isLoading && matches.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }

    // Create match dialog
    if (showCreateDialog) {
        CreateCricketMatchDialog(
            onDismiss = { showCreateDialog = false },
            onCreateMatch = { teamA, teamB, matchType, overs ->
                viewModel.createCricketMatch(teamA, teamB, matchType, overs) { matchId ->
                    showCreateDialog = false
                    onNavigateToAdmin(matchId) // Navigate directly to admin panel
                }
            }
        )
    }
}

@Composable
fun CricketMatchCard(
    match: CricketMatch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Match header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.teamA} vs ${match.teamB}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                MatchStatusChip(status = match.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Match type and overs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.matchType,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${match.totalOvers} overs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current score or match status
            if (match.status == MatchStatus.IN_PROGRESS && match.innings.isNotEmpty()) {
                val currentInnings = match.innings.lastOrNull()
                currentInnings?.let { innings ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${innings.battingTeam}: ${innings.totalRuns}/${innings.totalWickets}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${innings.totalOvers} overs",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Text(
                    text = when (match.status) {
                        MatchStatus.SCHEDULED -> "Match Scheduled"
                        MatchStatus.COMPLETED -> "Match Completed"
                        MatchStatus.CANCELLED -> "Match Cancelled"
                        MatchStatus.ABANDONED -> "Match Abandoned"
                        else -> "Match Status: ${match.status.name}"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MatchStatusChip(status: MatchStatus) {
    val (text, color) = when (status) {
        MatchStatus.SCHEDULED -> "Scheduled" to Color.Blue
        MatchStatus.IN_PROGRESS -> "LIVE" to Color.Red
        MatchStatus.COMPLETED -> "Completed" to Color.Green
        MatchStatus.CANCELLED -> "Cancelled" to Color.Gray
        MatchStatus.ABANDONED -> "Abandoned" to Color.Red
    }

    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCricketMatchDialog(
    onDismiss: () -> Unit,
    onCreateMatch: (String, String, String, Int) -> Unit
) {
    var teamA by remember { mutableStateOf("") }
    var teamB by remember { mutableStateOf("") }
    var matchType by remember { mutableStateOf("T20") }
    var overs by remember { mutableStateOf(20) }

    val matchTypes = listOf("T20", "ODI", "Test")
    val oversOptions = mapOf("T20" to 20, "ODI" to 50, "Test" to 90)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Cricket Match") },
        text = {
            Column {
                OutlinedTextField(
                    value = teamA,
                    onValueChange = { teamA = it },
                    label = { Text("Team A") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = teamB,
                    onValueChange = { teamB = it },
                    label = { Text("Team B") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Match type dropdown
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = matchType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Match Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        matchTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    matchType = type
                                    overs = oversOptions[type] ?: 20
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Overs input
                OutlinedTextField(
                    value = overs.toString(),
                    onValueChange = { 
                        val newOvers = it.toIntOrNull()
                        if (newOvers != null && newOvers > 0) {
                            overs = newOvers
                        }
                    },
                    label = { Text("Overs") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (teamA.isNotBlank() && teamB.isNotBlank()) {
                        onCreateMatch(teamA.trim(), teamB.trim(), matchType, overs)
                    }
                },
                enabled = teamA.isNotBlank() && teamB.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketMatchDetailScreen(
    matchId: String,
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToViewer: () -> Unit = {}
) {
    val viewModel: CricketMatchDetailViewModel = viewModel { CricketMatchDetailViewModel(matchId) }

    val match by viewModel.match.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val error by viewModel.error.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cricket Match") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refreshMatch) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                    if (isAdmin) {
                        IconButton(onClick = onNavigateToAdmin) {
                            Icon(Icons.Default.AdminPanelSettings, "Admin Panel", tint = Color(0xFF2E7D32))
                        }
                    } else {
                        IconButton(onClick = onNavigateToViewer) {
                            Icon(Icons.Default.Visibility, "Live View", tint = Color(0xFF2E7D32))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Connection status
            CricketConnectionStatusCard(connectionState)

            Spacer(modifier = Modifier.height(16.dp))

            // Error display
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = viewModel::clearError
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            match?.let { currentMatch ->
                CricketMatchScoreCard(
                    match = currentMatch,
                    isAdmin = isAdmin,
                    onScoreUpdate = viewModel::updateCricketScore,
                    onInningsUpdate = viewModel::updateInnings,
                    onBowlerUpdate = viewModel::updateBowler,
                    onBatsmanUpdate = viewModel::updateBatsman
                )
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun CricketConnectionStatusCard(connectionState: CricketWebSocketManager.ConnectionState) {
    val (text, color) = when (connectionState) {
        CricketWebSocketManager.ConnectionState.CONNECTING -> "Connecting..." to Color.Blue
        CricketWebSocketManager.ConnectionState.CONNECTED -> "Live Updates Active" to Color.Green
        CricketWebSocketManager.ConnectionState.DISCONNECTED -> "Disconnected" to Color.Gray
        CricketWebSocketManager.ConnectionState.ERROR -> "Connection Error" to Color.Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@Composable
fun CricketMatchScoreCard(
    match: CricketMatch,
    isAdmin: Boolean,
    onScoreUpdate: (CricketScorePayload) -> Unit,
    onInningsUpdate: (CricketInnings) -> Unit,
    onBowlerUpdate: (Bowler) -> Unit,
    onBatsmanUpdate: (Batsman) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Match header
            Text(
                text = "CRICKET MATCH",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${match.teamA} vs ${match.teamB}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${match.matchType} • ${match.totalOvers} overs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Current innings display
            if (match.innings.isNotEmpty()) {
                val currentInnings = match.innings.lastOrNull()
                currentInnings?.let { innings ->
                    CricketInningsDisplay(
                        innings = innings,
                        isAdmin = isAdmin,
                        onScoreUpdate = onScoreUpdate,
                        onInningsUpdate = onInningsUpdate,
                        onBowlerUpdate = onBowlerUpdate,
                        onBatsmanUpdate = onBatsmanUpdate
                    )
                }
            } else {
                Text(
                    text = "Match not started yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CricketInningsDisplay(
    innings: CricketInnings,
    isAdmin: Boolean,
    onScoreUpdate: (CricketScorePayload) -> Unit,
    onInningsUpdate: (CricketInnings) -> Unit,
    onBowlerUpdate: (Bowler) -> Unit,
    onBatsmanUpdate: (Batsman) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Innings header
        Text(
            text = "Innings ${innings.inningsNumber}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${innings.battingTeam} batting",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score display
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${innings.totalRuns}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Runs",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "/",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${innings.totalWickets}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Wickets",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overs and run rate
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.1f", innings.totalOvers),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Overs",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.2f", innings.runRate),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Run Rate",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Admin controls
        if (isAdmin) {
            CricketAdminControls(
                innings = innings,
                onScoreUpdate = onScoreUpdate,
                onInningsUpdate = onInningsUpdate,
                onBowlerUpdate = onBowlerUpdate,
                onBatsmanUpdate = onBatsmanUpdate
            )
        }
    }
}

@Composable
fun CricketAdminControls(
    innings: CricketInnings,
    onScoreUpdate: (CricketScorePayload) -> Unit,
    onInningsUpdate: (CricketInnings) -> Unit,
    onBowlerUpdate: (Bowler) -> Unit,
    onBatsmanUpdate: (Batsman) -> Unit
) {
    var showScoreDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Admin Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showScoreDialog = true }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update Score")
                }

                Button(
                    onClick = { /* Handle over completion */ }
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Next Over")
                }
            }
        }
    }

    if (showScoreDialog) {
        CricketScoreUpdateDialog(
            innings = innings,
            onDismiss = { showScoreDialog = false },
            onUpdate = { scoreUpdate ->
                onScoreUpdate(scoreUpdate)
                showScoreDialog = false
            }
        )
    }
}

@Composable
fun CricketScoreUpdateDialog(
    innings: CricketInnings,
    onDismiss: () -> Unit,
    onUpdate: (CricketScorePayload) -> Unit
) {
    var runs by remember { mutableStateOf(innings.totalRuns.toString()) }
    var wickets by remember { mutableStateOf(innings.totalWickets.toString()) }
    var overs by remember { mutableStateOf(innings.totalOvers.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Score") },
        text = {
            Column {
                OutlinedTextField(
                    value = runs,
                    onValueChange = { runs = it },
                    label = { Text("Runs") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = wickets,
                    onValueChange = { wickets = it },
                    label = { Text("Wickets") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = overs,
                    onValueChange = { overs = it },
                    label = { Text("Overs") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val runsInt = runs.toIntOrNull() ?: innings.totalRuns
                    val wicketsInt = wickets.toIntOrNull() ?: innings.totalWickets
                    val oversDouble = overs.toDoubleOrNull() ?: innings.totalOvers
                    val runRate = if (oversDouble > 0) runsInt / oversDouble else 0.0
                    
                    onUpdate(
                        CricketScorePayload(
                            runs = runsInt,
                            wickets = wicketsInt,
                            overs = oversDouble,
                            runRate = runRate
                        )
                    )
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CricketMatchApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "cricket-matches"
    ) {
        composable("cricket-matches") {
            CricketMatchListScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate("cricket-match/$matchId")
                },
                onNavigateToAdmin = { matchId ->
                    navController.navigate("cricket-admin/$matchId")
                }
            )
        }

        composable(
            "cricket-match/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            CricketMatchDetailScreen(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdmin = { 
                    navController.navigate("cricket-admin/$matchId")
                },
                onNavigateToViewer = {
                    navController.navigate("cricket-viewer/$matchId")
                }
            )
        }
        
        composable(
            "cricket-admin/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            CricketAdminPanelNew(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            "cricket-viewer/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            CricketViewerScreen(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
