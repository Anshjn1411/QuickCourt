// presentation/screens/livematches/CricketAdminPanel.kt
package com.project.odoo_235.presentation.screens.user.screen.livematches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.odoo_235.data.models.*
import com.project.odoo_235.presentation.viewmodels.CricketMatchDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketAdminPanel(
    matchId: String,
    onNavigateBack: () -> Unit
) {
    val viewModel: CricketMatchDetailViewModel = viewModel { CricketMatchDetailViewModel(matchId) }
    val match by viewModel.match.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    // Set admin mode
    LaunchedEffect(Unit) {
        viewModel.setAdminStatus(true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cricket Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setAdminStatus(false) }) {
                        Icon(Icons.Default.ExitToApp, "Exit Admin")
                    }
                }
            )
        }
    ) { paddingValues ->
        match?.let { currentMatch ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MatchOverviewCard(match = currentMatch)
                }

                item {
                    MatchStatusControls(
                        match = currentMatch,
                        onStatusUpdate = { status ->
                            viewModel.updateMatchStatus(status)
                        }
                    )
                }

                if (currentMatch.innings.isNotEmpty()) {
                    item {
                        CurrentInningsCard(
                            innings = currentMatch.innings.last(),
                            onScoreUpdate = viewModel::updateCricketScore,
                            onInningsUpdate = viewModel::updateInnings
                        )
                    }
                }

                item {
                    BatsmanManagementCard(
                        match = currentMatch,
                        onBatsmanUpdate = viewModel::updateBatsman
                    )
                }

                item {
                    BowlerManagementCard(
                        match = currentMatch,
                        onBowlerUpdate = viewModel::updateBowler
                    )
                }

                item {
                    BallByBallControls(
                        match = currentMatch,
                        onScoreUpdate = viewModel::updateCricketScore
                    )
                }
            }
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

@Composable
fun MatchOverviewCard(match: CricketMatch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Match Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Teams",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${match.teamA} vs ${match.teamB}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    Text(
                        text = "Format",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${match.matchType} • ${match.totalOvers} overs",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    MatchStatusChip(status = match.status)
                }

                Column {
                    Text(
                        text = "Current Innings",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Innings ${match.currentInnings}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MatchStatusControls(
    match: CricketMatch,
    onStatusUpdate: (MatchStatus) -> Unit
) {
    var showStatusDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Match Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onStatusUpdate(MatchStatus.IN_PROGRESS) },
                    enabled = match.status == MatchStatus.SCHEDULED
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start Match")
                }

                Button(
                    onClick = { onStatusUpdate(MatchStatus.COMPLETED) },
                    enabled = match.status == MatchStatus.IN_PROGRESS
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("End Match")
                }

                Button(
                    onClick = { showStatusDialog = true }
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("More")
                }
            }
        }
    }

    if (showStatusDialog) {
        MatchStatusDialog(
            currentStatus = match.status,
            onDismiss = { showStatusDialog = false },
            onStatusSelected = { status ->
                onStatusUpdate(status)
                showStatusDialog = false
            }
        )
    }
}

@Composable
fun MatchStatusDialog(
    currentStatus: MatchStatus,
    onDismiss: () -> Unit,
    onStatusSelected: (MatchStatus) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Match Status") },
        text = {
            Column {
                MatchStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentStatus == status,
                            onClick = { onStatusSelected(status) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = status.name.replace("_", " "),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CurrentInningsCard(
    innings: CricketInnings,
    onScoreUpdate: (CricketScorePayload) -> Unit,
    onInningsUpdate: (CricketInnings) -> Unit
) {
    var showScoreDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Current Innings - ${innings.battingTeam}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Score display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${innings.totalRuns}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Runs",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${innings.totalWickets}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Wickets",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", innings.totalOvers),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Overs",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick score update buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showScoreDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Update Score")
                }

                Button(
                    onClick = { /* Handle over completion */ },
                    modifier = Modifier.weight(1f)
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
fun BatsmanManagementCard(
    match: CricketMatch,
    onBatsmanUpdate: (Batsman) -> Unit
) {
    var showBatsmanDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "Batsmen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showBatsmanDialog = true }
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Batsman")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (match.innings.isNotEmpty()) {
                val currentInnings = match.innings.last()
                if (currentInnings.batsmen.isNotEmpty()) {
                    currentInnings.batsmen.forEach { batsman ->
                        BatsmanRow(
                            batsman = batsman,
                            onUpdate = onBatsmanUpdate
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(
                        text = "No batsmen added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showBatsmanDialog) {
        AddBatsmanDialog(
            onDismiss = { showBatsmanDialog = false },
            onAdd = { batsman ->
                onBatsmanUpdate(batsman)
                showBatsmanDialog = false
            }
        )
    }
}

@Composable
fun BatsmanRow(
    batsman: Batsman,
    onUpdate: (Batsman) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (batsman.isOnStrike) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = batsman.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${batsman.runs} (${batsman.balls}) • ${batsman.fours}×4 • ${batsman.sixes}×6",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                if (batsman.isOnStrike) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "STRIKE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (batsman.isOut) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.error,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "OUT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BowlerManagementCard(
    match: CricketMatch,
    onBowlerUpdate: (Bowler) -> Unit
) {
    var showBowlerDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "Bowlers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { showBowlerDialog = true }
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Bowler")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (match.innings.isNotEmpty()) {
                val currentInnings = match.innings.last()
                if (currentInnings.bowlers.isNotEmpty()) {
                    currentInnings.bowlers.forEach { bowler ->
                        BowlerRow(
                            bowler = bowler,
                            onUpdate = onBowlerUpdate
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(
                        text = "No bowlers added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showBowlerDialog) {
        AddBowlerDialog(
            onDismiss = { showBowlerDialog = false },
            onAdd = { bowler ->
                onBowlerUpdate(bowler)
                showBowlerDialog = false
            }
        )
    }
}

@Composable
fun BowlerRow(
    bowler: Bowler,
    onUpdate: (Bowler) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (bowler.isBowling) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = bowler.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${bowler.overs} overs • ${bowler.runs} runs • ${bowler.wickets} wickets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (bowler.isBowling) {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BOWLING",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BallByBallControls(
    match: CricketMatch,
    onScoreUpdate: (CricketScorePayload) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ball by Ball Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick run buttons
            Text(
                text = "Quick Runs",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0, 1, 2, 3, 4, 6).forEach { runs ->
                    Button(
                        onClick = {
                            // Handle run addition
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("$runs")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Special ball buttons
            Text(
                text = "Special Balls",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Handle wide */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Wide")
                }

                Button(
                    onClick = { /* Handle no ball */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("No Ball")
                }

                Button(
                    onClick = { /* Handle wicket */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Wicket")
                }
            }
        }
    }
}

@Composable
fun AddBatsmanDialog(
    onDismiss: () -> Unit,
    onAdd: (Batsman) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Batsman") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Batsman Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            Batsman(
                                name = name.trim(),
                                isOnStrike = true
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
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
fun AddBowlerDialog(
    onDismiss: () -> Unit,
    onAdd: (Bowler) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bowler") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Bowler Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            Bowler(
                                name = name.trim(),
                                isBowling = true
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
