package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentModeScreen(
    bookingViewModel: BookingViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    val pending by bookingViewModel.pendingBooking.collectAsState()
    val paymentVM: PaymentViewModel = viewModel()
    val paymentStatus by paymentVM.paymentStatus.collectAsState()

    LaunchedEffect(paymentStatus) {
        val p = pending ?: return@LaunchedEffect
        when (paymentStatus) {
            is PaymentStatus.Success -> {
                bookingViewModel.bookVenueDirect(
                    venueId = p.venueId,
                    selectedDate = LocalDate.parse(p.dateIso),
                    selectedSlot = p.slotRange,
                    paymentMethod = "upi"
                )
                onSuccess()
            }
            is PaymentStatus.Failed -> {
                Toast.makeText(context, "Payment failed", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
                Text(
                    text = "Choose Payment Method",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            PaymentOptionCard(
                title = "Cash on Arrival",
                subtitle = "Pay at venue. Advance not required here.",
                icon = Icons.Default.Payments,
                container = Color(0xFFE8F5E8)
            ) {
                val p = pending
                if (p == null) {
                    Toast.makeText(context, "Selection expired. Go back.", Toast.LENGTH_SHORT).show()
                    return@PaymentOptionCard
                }
                bookingViewModel.bookVenueDirect(
                    venueId = p.venueId,
                    selectedDate = LocalDate.parse(p.dateIso),
                    selectedSlot = p.slotRange,
                    paymentMethod = "cash"
                )
                onSuccess()
            }

            Spacer(Modifier.height(12.dp))

            PaymentOptionCard(
                title = "UPI (PhonePe / Google Pay)",
                subtitle = "Pay securely via UPI apps",
                icon = Icons.Default.QrCode,
                container = Color(0xFFE3F2FD)
            ) {
                val p = pending
                if (p == null) {
                    Toast.makeText(context, "Selection expired. Go back.", Toast.LENGTH_SHORT).show()
                    return@PaymentOptionCard
                }
                // Use existing Razorpay flow to collect UPI payment
                paymentVM.initiatePayment(activity)
            }

            Spacer(Modifier.height(12.dp))

            PaymentOptionCard(
                title = "Card / Netbanking (Razorpay)",
                subtitle = "Pay online using Razorpay",
                icon = Icons.Default.CreditCard,
                container = Color(0xFFFFF3E0)
            ) {
                val p = pending
                if (p == null) {
                    Toast.makeText(context, "Selection expired. Go back.", Toast.LENGTH_SHORT).show()
                    return@PaymentOptionCard
                }
                // For Razorpay card/netbanking, still collect via Razorpay
                paymentVM.initiatePayment(activity)
            }
        }
    }
}

@Composable
private fun PaymentOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    container: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF4CAF50))
            }
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = Color.Transparent)
        }
    }
}


