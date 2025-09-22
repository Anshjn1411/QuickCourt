package com.project.odoo_235.presentation.screens.user.screen.MianScreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.odoo_235.data.models.*

@Composable
fun NearMeScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val nearby by viewModel.nearbyCourts.collectAsState()
    val params by viewModel.nearbyParams.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var radius by remember { mutableStateOf("10") }
    var sport by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Row {
            OutlinedTextField(value = radius, onValueChange = { radius = it }, label = { Text("Radius km") }, singleLine = true, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = sport, onValueChange = { sport = it }, label = { Text("Sport") }, singleLine = true, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = maxPrice, onValueChange = { maxPrice = it }, label = { Text("Max Price") }, singleLine = true, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            viewModel.searchNearby(
                context = context,
                radiusKm = radius.toIntOrNull() ?: 10,
                sportType = sport.ifBlank { null },
                maxPrice = maxPrice.ifBlank { null }
            )
        }) { Text("Search Near Me") }

        Spacer(Modifier.height(8.dp))
        if (loading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        } else {
            if (params != null) {
                Text("Results around (${params!!.lat}, ${params!!.lng}) within ${params!!.radius}km")
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(nearby, key = { it.id }) { court ->
                    NearCourtRow(court) { navController.navigate("Booking/${court.id}") }
                }
            }
        }
    }
}

@Composable
private fun NearCourtRow(court: Court, onOpen: () -> Unit) {
    Card {
        Column(Modifier.padding(12.dp)) {
            Text(court.name, style = MaterialTheme.typography.titleMedium)
            Text("${court.location.city}, ${court.location.state}")
            Text("⭐ ${"%.1f".format(court.ratings.average)} (${court.ratings.totalReviews})")
            Spacer(Modifier.height(6.dp))
            Button(onClick = onOpen) { Text("Open") }
        }
    }
}