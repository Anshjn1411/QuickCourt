// presentation/screens/bookingScreen/BookingDetailsScreen.kt
package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.odoo_235.data.models.Booking
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(navController: NavController, vm: BookingViewModel, bookingId: String) {
    val detail by vm.bookingDetail.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var showCancel by remember { mutableStateOf(false) }
    var showReview by remember { mutableStateOf(false) }
    var showPayment by remember { mutableStateOf(false) }

    LaunchedEffect(bookingId) { vm.fetchBookingDetails(bookingId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            when {
                loading -> CircularProgressIndicator()
                !error.isNullOrBlank() -> Text(error ?: "")
                detail == null -> Text("Not found")
                else -> DetailBody(
                    b = detail!!,
                    onCancel = { showCancel = true },
                    onReview = { showReview = true },
                    onPayment = { showPayment = true }
                )
            }
        }
    }

    if (showCancel && detail != null) CancelBookingDialog(vm, detail!!.id) { showCancel = false }
    if (showReview && detail != null) BookingReviewDialog(vm, detail!!.id) { showReview = false }
    if (showPayment && detail != null) PaymentUpdateDialog(vm, detail!!.id) { showPayment = false }
}

@Composable
private fun DetailBody(b: Booking, onCancel: () -> Unit, onReview: () -> Unit, onPayment: () -> Unit) {
    val court = (b.court as? Map<*, *>)
    val courtName = court?.get("name")?.toString() ?: "Court"
    val city = ((court?.get("location") as? Map<*, *>)?.get("city")?.toString())
    val images = (court?.get("images") as? List<*>)
    val firstUrl = images?.firstOrNull()?.let { (it as? Map<*, *>)?.get("url")?.toString() }

    // Header with image and chips
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(220.dp).background(Color(0xFFEFEFEF))) {
            if (!firstUrl.isNullOrBlank()) {
                AsyncImage(model = firstUrl, contentDescription = courtName, modifier = Modifier.fillMaxSize())
            }
            Row(Modifier.align(Alignment.TopStart).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(text = b.status)
                StatusChip(text = b.paymentStatus)
            }
        }

        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(courtName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (!city.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(city, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Date", "${b.date}")
                    DetailRow("Time", "${b.startTime} - ${b.endTime}")
                    DetailRow("Players", b.players.count.toString())
                    DetailRow("Payment", listOfNotNull(b.paymentStatus, b.paymentMethod).joinToString(" • "))
                    b.transactionId?.let { DetailRow("Transaction", it) }
                    DetailRow("Total", "₹${b.totalAmount}")
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onCancel,
                    enabled = b.status in listOf("pending","confirmed"),
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel Booking") }

                OutlinedButton(
                    onClick = onPayment,
                    enabled = b.paymentStatus in listOf("pending","failed"),
                    modifier = Modifier.weight(1f)
                ) { Text("Update Payment") }

                Button(
                    onClick = onReview,
                    enabled = b.status == "completed" && b.rating == null,
                    modifier = Modifier.weight(1f)
                ) { Text("Give Review") }
            }

            b.cancellationReason?.let {
                Spacer(Modifier.height(16.dp))
                ElevatedCard { Text("Cancellation: $it", modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    val color = when (text.lowercase()) {
        "confirmed" -> Color(0xFF4CAF50)
        "completed" -> Color(0xFF2196F3)
        "cancelled" -> Color(0xFFF44336)
        "pending" -> Color(0xFFFF9800)
        "paid" -> Color(0xFF4CAF50)
        "failed" -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.secondary
    }
    AssistChip(onClick = {}, label = { Text(text.uppercase()) }, colors = AssistChipDefaults.assistChipColors(containerColor = color.copy(alpha = 0.15f), labelColor = color))
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CancelBookingDialog(vm: BookingViewModel, bookingId: String, onDismiss: () -> Unit) {
    var reason by remember { mutableStateOf("") }
    val submitting by vm.cancelSubmitting.collectAsState()
    AlertDialog(
        onDismissRequest = { if (!submitting) onDismiss() },
        title = { Text("Cancel booking") },
        text = { OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason") }, minLines = 3) },
        confirmButton = {
            Button(enabled = reason.isNotBlank() && !submitting, onClick = {
                vm.cancelBooking(bookingId, reason) { _, _ -> onDismiss() }
            }) { if (submitting) CircularProgressIndicator(Modifier.size(16.dp)) else Text("Submit") }
        },
        dismissButton = { TextButton(enabled = !submitting, onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
fun BookingReviewDialog(vm: BookingViewModel, bookingId: String, onDismiss: () -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var review by remember { mutableStateOf("") }
    val submitting by vm.reviewSubmitting.collectAsState()
    AlertDialog(
        onDismissRequest = { if (!submitting) onDismiss() },
        title = { Text("Rate your booking") },
        text = {
            Column {
                Row {
                    repeat(5) { i ->
                        IconToggleButton(checked = i < rating, onCheckedChange = { rating = i + 1 }) {
                            Icon(if (i < rating) Icons.Filled.Star else Icons.Outlined.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = review, onValueChange = { review = it }, label = { Text("Review (optional)") }, minLines = 3)
            }
        },
        confirmButton = {
            Button(enabled = !submitting, onClick = {
                vm.addBookingReview(bookingId, rating, review.ifBlank { null }) { _, _ -> onDismiss() }
            }) { if (submitting) CircularProgressIndicator(Modifier.size(16.dp)) else Text("Submit") }
        },
        dismissButton = { TextButton(enabled = !submitting, onClick = onDismiss) { Text("Close") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentUpdateDialog(vm: BookingViewModel, bookingId: String, onDismiss: () -> Unit) {
    val methods = listOf("online","upi","card","cash")
    var method by remember { mutableStateOf("online") }
    var status by remember { mutableStateOf("paid") } // or "failed"
    var txnId by remember { mutableStateOf("") }
    val updating by vm.paymentUpdating.collectAsState()

    AlertDialog(
        onDismissRequest = { if (!updating) onDismiss() },
        title = { Text("Update payment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = false, onExpandedChange = { }) {
                    Text("Method")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    methods.forEach {
                        FilterChip(selected = method == it, onClick = { method = it }, label = { Text(it.uppercase()) })
                    }
                }
                Text("Status")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = status == "paid", onClick = { status = "paid" }, label = { Text("PAID") })
                    FilterChip(selected = status == "failed", onClick = { status = "failed" }, label = { Text("FAILED") })
                }
                OutlinedTextField(value = txnId, onValueChange = { txnId = it }, label = { Text("Transaction ID (optional)") })
            }
        },
        confirmButton = {
            Button(enabled = !updating, onClick = {
                vm.updatePayment(
                    id = bookingId,
                    paymentStatus = status,
                    paymentMethod = method,
                    transactionId = txnId.ifBlank { null }
                ) { _, _ -> onDismiss() }
            }) { if (updating) CircularProgressIndicator(Modifier.size(16.dp)) else Text("Save") }
        },
        dismissButton = { TextButton(enabled = !updating, onClick = onDismiss) { Text("Close") } }
    )
}