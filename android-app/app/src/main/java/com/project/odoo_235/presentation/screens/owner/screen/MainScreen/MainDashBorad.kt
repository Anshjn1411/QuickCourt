package com.project.odoo_235.presentation.screens.owner.mainscreen



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.data.models.*
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.FilterSection
import com.project.odoo_235.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacilityDashboardScreen(
    navController: NavController,
    viewModel: FacilityDashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val session = remember { UserSessionManager(context) }
    val cachedUser by session.userData.collectAsState(initial = null)

    LaunchedEffect(cachedUser?.id) {
        if (cachedUser != null && cachedUser!!.role == "Owner") {
            viewModel.refreshAll()
        }
    }

    val courts by viewModel.courts.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val ownerStats by viewModel.ownerStats.collectAsState()
    val ownerAnalytics by viewModel.ownerAnalytics.collectAsState()
    val totalCourts by viewModel.totalCourts.collectAsState()
    val totalBookings by viewModel.totalBookings.collectAsState()
    val totalEarnings by viewModel.totalEarnings.collectAsState()
    val availableSports by viewModel.availableSports.collectAsState()
    val availableStatuses by viewModel.availableStatuses.collectAsState()

    // Filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Update filters when state changes
    LaunchedEffect(searchQuery) {
        viewModel.setSearchQuery(searchQuery)
    }
    LaunchedEffect(selectedSport) {
        viewModel.setSportFilter(selectedSport)
    }
    LaunchedEffect(selectedStatus) {
        viewModel.setStatusFilter(selectedStatus)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner Dashboard") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, null)
                    }
                    IconButton(onClick = { viewModel.refreshAll() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        when {
            loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(error ?: "Error")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        WelcomeHeader(cachedUser?.name ?: "Owner")
                    }

//                    // Filter Section
//                    if (showFilters) {
//                        item {
//                            FilterSection(
//                                searchQuery = searchQuery,
//                                onSearchQueryChange = { searchQuery = it },
//                                selectedSport = selectedSport,
//                                onSportSelected = { selectedSport = it },
//                                selectedStatus = selectedStatus,
//                                onStatusSelected = { selectedStatus = it },
//                                availableSports = availableSports,
//                                availableStatuses = availableStatuses,
//                                onClearFilters = {
//                                    searchQuery = ""
//                                    selectedSport = null
//                                    selectedStatus = null
//                                    viewModel.clearAllFilters()
//                                },
//                                focusManager = focusManager
//                            )
//                        }
//                    }

                    item {
                        AnalyticsSection(totalCourts, totalBookings, totalEarnings)
                    }
                    item {
                        OwnerStatsSection(ownerStats)
                    }
                    if (ownerAnalytics != null) {
                        item {
                            OwnerAnalyticsSection(ownerAnalytics!!)
                        }
                    }
                    item {
                        CourtsSection(courts = courts)
                    }
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(AppColors.Primary, AppColors.Primary.copy(0.8f))))
                .padding(20.dp)
        ) {
            Column {
                Text("Welcome back, $userName! 👋", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Here’s your facility performance", color = Color.White.copy(0.9f))
            }
        }
    }
}

@Composable
private fun AnalyticsSection(totalCourts: Int, totalBookings: Int, totalEarnings: Double) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        AnalyticsCard("Total Courts", totalCourts.toString(), Icons.Default.SportsTennis, Color(0xFF4CAF50))
        AnalyticsCard("Bookings", totalBookings.toString(), Icons.Default.CalendarMonth, Color(0xFF2196F3))
        AnalyticsCard("Earnings", "₹${"%.0f".format(totalEarnings)}", Icons.Default.Payments, Color(0xFFFF9800))
    }
}

@Composable
private fun AnalyticsCard(title: String, value: String, icon: ImageVector, color: Color) {
    Card(Modifier.height(110.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = color)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(title, color = AppColors.OnSurfaceVariant, fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}

@Composable
private fun OwnerStatsSection(stats: OwnerCourtStatistics?) {
    if (stats == null) return
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Snapshot (last ${stats.recentCourts} recent)", fontWeight = FontWeight.SemiBold)
            Text("Active courts: ${stats.activeCourts}")
            Text("Pending approval: ${stats.pendingApproval}")
            Text("Average rating: ${"%.1f".format(stats.averageRating)}")
        }
    }
}

@Composable
private fun OwnerAnalyticsSection(analytics: OwnerAnalytics) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Revenue by Sport", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (analytics.revenueBySport.isEmpty()) {
            Text("No data", color = AppColors.OnSurfaceVariant)
        } else {
            SimpleBarChart(
                data = analytics.revenueBySport.values.map { it.toFloat() },
                labels = analytics.revenueBySport.keys.toList(),
                heightDp = 160.dp
            )
        }

        Text("Bookings by Sport", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (analytics.bookingsBySport.isEmpty()) {
            Text("No data", color = AppColors.OnSurfaceVariant)
        } else {
            SimpleBarChart(
                data = analytics.bookingsBySport.values.map { it.toFloat() },
                labels = analytics.bookingsBySport.keys.toList(),
                heightDp = 140.dp,
                barColor = AppColors.Primary
            )
        }

        Text("Top Courts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(analytics.courtPerformance) { perf ->
                Card {
                    Column(Modifier.padding(12.dp)) {
                        Text(perf.name, fontWeight = FontWeight.SemiBold)
                        Text("${perf.sportType} • Bookings: ${perf.bookings}")
                        Text("Revenue: ₹${"%.0f".format(perf.revenue)} • ⭐ ${"%.1f".format(perf.rating)}")
                        Text("Status: ${perf.status} • ${perf.approvalStatus}")
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleBarChart(
    data: List<Float>,
    labels: List<String>,
    heightDp: Dp,
    barColor: Color = AppColors.Primary
) {
    if (data.isEmpty()) return
    val maxVal = data.maxOrNull() ?: 1f
    Row(
        Modifier.fillMaxWidth().height(heightDp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { i, v ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((v / maxVal).coerceAtLeast(0.05f) * (heightDp - 40.dp))
                        .background(brush = Brush.verticalGradient(listOf(barColor, barColor.copy(alpha = 0.7f))))
                )
                Spacer(Modifier.height(6.dp))
                Text(labels.getOrNull(i) ?: "", textAlign = TextAlign.Center, maxLines = 1, color = AppColors.OnSurfaceVariant, fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
        }
    }
}

@Composable
private fun CourtsSection(courts: List<Court>) {
    Column {
        Text("Your Courts (${courts.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (courts.isEmpty()) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SportsTennis, null, tint = AppColors.OnSurfaceVariant)
                    Text("No courts yet")
                    Text("Add a court from Actions", color = AppColors.OnSurfaceVariant)
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(courts) { court -> CourtCard(court) }
            }
        }
    }
}

@Composable
private fun CourtCard(court: Court) {
    Card(Modifier.width(280.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(court.name, fontWeight = FontWeight.SemiBold)
                    Text(court.sportType, color = AppColors.Primary)
                }
                AssistChip(
                    onClick = {},
                    label = { Text(if (court.availability.isAvailable) "Available" else "Unavailable") },
                    leadingIcon = { Icon(Icons.Default.Schedule, null) }
                )
            }
            Text("${court.location.city}, ${court.location.state}", color = AppColors.OnSurfaceVariant)
            Text("Base: ${court.pricing.currency} ${court.pricing.basePrice}", color = AppColors.OnSurfaceVariant)
            Text("Status: ${court.status} • ${court.approvalStatus}", color = AppColors.OnSurfaceVariant)
        }
    }
}





// Data classes for analytics
data class BookingTrend(val label: String, val count: Int)
data class EarningsSummary(val label: String, val amount: Double)
data class HourlyBooking(val hour: Int, val count: Int)