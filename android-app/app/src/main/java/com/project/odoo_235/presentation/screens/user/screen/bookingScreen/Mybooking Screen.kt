// presentation/screens/bookingScreen/MyBookingsScreen.kt
package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.odoo_235.data.models.Booking
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.MainViewModel
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtBottomBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtSearchBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtTopBar
import com.project.odoo_235.ui.theme.AppColors
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(navController: NavController, vm: BookingViewModel , mainViewModel: MainViewModel, currentRoute : String = "My_Booking") {
    val bookings by vm.bookings.collectAsState()
    val pagination by vm.pagination.collectAsState()
    val loading by vm.listLoading.collectAsState()
    val error by vm.listError.collectAsState()

    var sortBy by remember { mutableStateOf("createdAt") }
    var sortOrder by remember { mutableStateOf("desc") }
    var status by remember { mutableStateOf<String?>(null) }
    val userName by mainViewModel.userName.collectAsState()
    val currentCity by mainViewModel.currentCity.collectAsState()
    val locationLoading by mainViewModel.locationLoading.collectAsState()

    LaunchedEffect(Unit) { vm.fetchUserBookings() }

    Scaffold(
        topBar = {
            QuickCourtTopBar(
                userName = userName,
                location = currentCity,
                isLocationLoading = locationLoading,
                onRefreshLocation = { mainViewModel.refreshLocation() }
            )
        },
        bottomBar = {
            QuickCourtBottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        },
        containerColor = AppColors.Background
    ) { paddingValues ->
        var query by remember { mutableStateOf("") }

        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            QuickCourtSearchBar(
                query = query,
                onQueryChange = { query = it },
                onVoiceClick = { }
            )

            // Controls row
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SortControls(sortBy, sortOrder) { sb, so ->
                                sortBy = sb; sortOrder = so
                                vm.setListControls(sortBy = sortBy, sortOrder = sortOrder, status = status)
                            }
                            Spacer(Modifier.width(8.dp))
                            StatusFilter(status) { new ->
                                status = new
                                vm.setListControls(sortBy = sortBy, sortOrder = sortOrder, status = status)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Page ${pagination?.currentPage ?: 1}/${pagination?.totalPages ?: 1}")
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { vm.prevPage() }, enabled = pagination?.hasPrevPage == true) { Text("Prev") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { vm.nextPage() }, enabled = pagination?.hasNextPage == true) { Text("Next") }
                    }
                }

            // Content list
                    when {
                        loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        !error.isNullOrBlank() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(error ?: "") }
                        bookings.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No bookings found") }
                else -> {
                    val filtered = bookings.filter { b ->
                        val courtName = (b.court as? Map<*, *>)?.get("name")?.toString()?.lowercase() ?: ""
                        val city = ((b.court as? Map<*, *>)?.get("location") as? Map<*, *>)?.get("city")?.toString()?.lowercase() ?: ""
                        query.isBlank() || courtName.contains(query.lowercase()) || city.contains(query.lowercase())
                    }
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                        items(filtered, key = { it.id }) { b ->
                            BookingRichCard(b) { navController.navigate("booking_details/${b.id}") }
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun SortControls(sortBy: String, sortOrder: String, onChange: (String, String) -> Unit) {
    var sortExpand by remember { mutableStateOf(false) }
    var orderExpand by remember { mutableStateOf(false) }
    val sortOptions = listOf("createdAt", "date", "status", "totalAmount")
    val orderOptions = listOf("asc", "desc")
    Row {
        Box {
            OutlinedButton(onClick = { sortExpand = true }) { Text("Sort: $sortBy") }
            DropdownMenu(expanded = sortExpand, onDismissRequest = { sortExpand = false }) {
                sortOptions.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { sortExpand = false; onChange(it, sortOrder) })
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Box {
            OutlinedButton(onClick = { orderExpand = true }) { Text("Order: $sortOrder") }
            DropdownMenu(expanded = orderExpand, onDismissRequest = { orderExpand = false }) {
                orderOptions.forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { orderExpand = false; onChange(sortBy, it) })
                }
            }
        }
    }
}

@Composable
private fun StatusFilter(current: String?, onChange: (String?) -> Unit) {
    val options = listOf(null, "pending", "confirmed", "completed", "cancelled", "no-show")
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) { Text("Status: ${current ?: "all"}") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt ?: "all") }, onClick = { expanded = false; onChange(opt) })
            }
        }
    }
}

@Composable
private fun BookingRichCard(b: Booking, onClick: () -> Unit) {
    val courtMap = (b.court as? Map<*, *>)
    val name = courtMap?.get("name")?.toString() ?: "Court"
    val location = ((courtMap?.get("location") as? Map<*, *>)?.get("city")?.toString() ?: "")
    val images = (courtMap?.get("images") as? List<*>)
    val firstUrl = images?.firstOrNull()?.let { (it as? Map<*, *>)?.get("url")?.toString() }
    val statusColor = when (b.status.lowercase()) {
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "cancelled" -> Color(0xFFF44336)
        else -> Color(0xFFFF9800)
    }
    ElevatedCard(
        Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(160.dp).background(Color(0xFFEFEFEF))) {
                if (!firstUrl.isNullOrBlank()) {
                    AsyncImage(model = firstUrl, contentDescription = name, modifier = Modifier.fillMaxSize())
                }
                Surface(
                    color = statusColor,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Text(b.status.uppercase(), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        Column(Modifier.padding(16.dp)) {
                Text(name, style = MaterialTheme.typography.titleMedium)
                if (location.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(location, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(8.dp))
                Text("${b.date}  ${b.startTime} - ${b.endTime}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text("Payment: ${b.paymentStatus}${b.paymentMethod?.let { " • ${it}" } ?: ""}", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))
                Text("Total: ₹${b.totalAmount}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}