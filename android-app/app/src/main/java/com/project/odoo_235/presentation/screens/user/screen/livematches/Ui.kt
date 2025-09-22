// presentation/screens/livematches/MatchScreens.kt
package com.project.odoo_235.presentation.screens.user.screen.livematches

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.odoo_235.data.models.Match
import com.project.odoo_235.data.repository.WebSocketManager
import com.project.odoo_235.presentation.viewmodels.MatchDetailViewModel
import com.project.odoo_235.presentation.viewmodels.MatchListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchListScreen(
    onNavigateToMatch: (String) -> Unit,
    viewModel: MatchListViewModel = viewModel()
) {
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Tracker") },
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
                        MatchCard(
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
        CreateMatchDialog(
            onDismiss = { showCreateDialog = false },
            onCreateMatch = { teamA, teamB ->
                viewModel.createMatch(teamA, teamB) { matchId ->
                    showCreateDialog = false
                    onNavigateToMatch(matchId)
                }
            }
        )
    }
}

@Composable
fun MatchCard(
    match: Match,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.teamA,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${match.scoreA}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.teamB,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${match.scoreB}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CreateMatchDialog(
    onDismiss: () -> Unit,
    onCreateMatch: (String, String) -> Unit
) {
    var teamA by remember { mutableStateOf("") }
    var teamB by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Match") },
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (teamA.isNotBlank() && teamB.isNotBlank()) {
                        onCreateMatch(teamA.trim(), teamB.trim())
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
fun MatchDetailScreen(
    matchId: String,
    onNavigateBack: () -> Unit
) {
    // Create ViewModel with factory pattern
    val viewModel: MatchDetailViewModel = viewModel { MatchDetailViewModel(matchId) }

    val match by viewModel.match.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refreshMatch) {
                        Icon(Icons.Default.Refresh, "Refresh")
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
            ConnectionStatusCard(connectionState)

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
                MatchScoreCard(
                    match = currentMatch,
                    onScoreUpdate = viewModel::updateScore
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
fun ConnectionStatusCard(connectionState: WebSocketManager.ConnectionState) {
    val (text, color) = when (connectionState) {
        WebSocketManager.ConnectionState.CONNECTING -> "Connecting..." to Color.Blue
        WebSocketManager.ConnectionState.CONNECTED -> "Live Updates Active" to Color.Green
        WebSocketManager.ConnectionState.DISCONNECTED -> "Disconnected" to Color.Gray
        WebSocketManager.ConnectionState.ERROR -> "Connection Error" to Color.Red
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
fun MatchScoreCard(
    match: Match,
    onScoreUpdate: (Int, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "LIVE MATCH",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Team A
            TeamScoreSection(
                teamName = match.teamA,
                score = match.scoreA,
                onScoreChange = { newScore ->
                    onScoreUpdate(newScore, match.scoreB)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Team B
            TeamScoreSection(
                teamName = match.teamB,
                score = match.scoreB,
                onScoreChange = { newScore ->
                    onScoreUpdate(match.scoreA, newScore)
                }
            )
        }
    }
}

@Composable
fun TeamScoreSection(
    teamName: String,
    score: Int,
    onScoreChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = teamName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = score.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { onScoreChange(maxOf(0, score - 1)) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Icon(Icons.Default.Remove, "Decrease")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { onScoreChange(score + 1) }
            ) {
                Icon(Icons.Default.Add, "Increase")
            }
        }
    }
}

@Composable
fun MatchApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "matches"
    ) {
        composable("matches") {
            MatchListScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate("match/$matchId")
                }
            )
        }

        composable(
            "match/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            MatchDetailScreen(
                matchId = matchId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}