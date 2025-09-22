package com.project.odoo_235.presentation.screens.user.screen.bookingScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.JsonObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBookingAnalyticsScreen(navController: NavController, vm: BookingViewModel) {
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val analytics by vm.analytics.collectAsState()

    LaunchedEffect(Unit) { vm.fetchUserAnalytics() }

    Scaffold(topBar = { TopAppBar(title = { Text("My Booking Stats") }) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when {
                loading -> CircularProgressIndicator()
                !error.isNullOrBlank() -> Text(error ?: "")
                analytics == null -> Text("No data")
                else -> AnalyticsCards(analytics!!)
            }
        }
    }
}

@Composable
private fun AnalyticsCards(obj: JsonObject) {
    val totals = obj.getAsJsonObject("totals") ?: JsonObject()
    val count = totals.get("count")?.asInt ?: 0
    val completed = totals.get("completed")?.asInt ?: 0
    val cancelled = totals.get("cancelled")?.asInt ?: 0
    val upcoming = totals.get("upcoming")?.asInt ?: 0
    val amountSpent = totals.get("amountSpent")?.asDouble ?: 0.0
    val currency = obj.get("currency")?.asString ?: "INR"

    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Total bookings: $count")
            Text("Completed: $completed")
            Text("Cancelled: $cancelled")
            Text("Upcoming: $upcoming")
            Text("Total spent: $currency $amountSpent")
            LinearProgressIndicator(progress = { if (count == 0) 0f else completed.toFloat() / count }, modifier = Modifier.fillMaxWidth())
        }
    }
}