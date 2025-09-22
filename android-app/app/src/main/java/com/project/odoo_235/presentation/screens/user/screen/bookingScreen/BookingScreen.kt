package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.project.odoo_235.data.models.Court
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingScreenNewUI(
    venue: Court,
    bookingViewModel: BookingViewModel,
    onBooked: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentStatus by paymentViewModel.paymentStatus.collectAsState()
    val availability by bookingViewModel.availability.collectAsState()

    var selectedFormat by remember { mutableStateOf("Box Cricket") }
    var selectedPitches by remember { mutableStateOf(1) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTimeSlotCategory by remember { mutableStateOf("Evening") }
    var selectedLabels by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(venue.id, selectedDate) {
        bookingViewModel.fetchAvailabilityForDate(venue.id, selectedDate.toString())
        selectedLabels = emptyList()
    }

    val labelToRangeMap by remember(availability) {
        mutableStateOf(availability.associate { slot ->
            val label = "${slot.startTime}-${slot.endTime}"
            label to label
        })
    }

    fun hourOf(time: String): Int = try { time.trim().substring(0,2).toInt() } catch(_: Exception){ 0 }

    val groupedByCategory = remember(availability) {
        availability.groupBy { slot ->
            val h = hourOf(slot.startTime)
            when {
                h in 5..7 -> "Twilight"
                h in 8..11 -> "Morning"
                h in 12..15 -> "Noon"
                else -> "Evening"
            }
        }.mapValues { entry ->
            entry.value.map { slot ->
                val label = "${slot.startTime}-${slot.endTime}"
                Triple(label, label, slot.available)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BookingHeader(
            venueName = venue.name,
            location = venue.location.city,
            onBackClick = { onBackClick?.invoke() }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                FormatSelection(
                    selectedFormat = selectedFormat,
                    onFormatSelected = { selectedFormat = it }
                )
            }

            item {
                PitchSelection(
                    selectedPitches = selectedPitches,
                    onPitchSelected = { selectedPitches = it }
                )
            }

            item {
                DateSelection(
                    selectedDate = selectedDate,
                    onDateSelected = { date -> selectedDate = date }
                )
            }

            item {
                val slotsInCategory = groupedByCategory[selectedTimeSlotCategory] ?: emptyList()
                TimeSlotSelectionDynamic(
                    categories = listOf("Twilight", "Morning", "Noon", "Evening"),
                    selectedCategory = selectedTimeSlotCategory,
                    onCategorySelected = { selectedTimeSlotCategory = it },
                    slots = slotsInCategory,
                    selectedLabels = selectedLabels,
                    onLabelToggle = { label ->
                        selectedLabels = if (selectedLabels.contains(label)) selectedLabels - label else selectedLabels + label
                    }
                )
            }
        }

        val priceText = "₹" + (venue.pricing.basePrice.toInt()).toString()
        val durationText = selectedLabels.firstOrNull() ?: "Select a slot"

        BookingBottomBar(
            price = priceText,
            duration = durationText,
            onProceedClick = {

                val chosen = selectedLabels.firstOrNull() ?: return@BookingBottomBar
                bookingViewModel.setPendingBooking(
                    venueId = venue.id,
                    dateIso = selectedDate.toString(),
                    slotRange = chosen
                )
                onBooked()
            }
        )
    }

    // Removed paymentStatus listener; payment is handled in PaymentModeScreen
}

@Composable
fun BookingHeader(
    venueName: String,
    location: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0F2F1))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                tint = Color.Black
                            )
                    }

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
                        Text(
                text = venueName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
                        )
                        Text(
                text = location,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FormatSelection(
    selectedFormat: String,
    onFormatSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
                            Text(
            text = "FORMAT",
            fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = { onFormatSelected("Box Cricket") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(36.dp)
        ) {
                            Text(
                text = selectedFormat,
                color = Color.White,
                                fontSize = 14.sp
                            )
        }
    }
}

@Composable
fun PitchSelection(
    selectedPitches: Int,
    onPitchSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
                                Text(
            text = "NO. OF PITCHES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Surface(
            shape = CircleShape,
            color = Color(0xFF4CAF50),
            modifier = Modifier.size(36.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                                Text(
                    text = selectedPitches.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateSelection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE8F5E8),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = "Offer",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Text(
                    text = "Flat ₹200 off",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Text(
                    text = "On all slots",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(7) { index ->
                val date = LocalDate.now().plusDays(index.toLong())
                val isSelected = date == selectedDate

                DateChip(
                    date = date,
                    isSelected = isSelected,
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateChip(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val dayOfMonth = date.dayOfMonth.toString()
    val month = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFF5F5F5),
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = dayOfWeek,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = dayOfMonth,
                fontSize = 16.sp,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = month,
                fontSize = 10.sp,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

@Composable
fun TimeSlotSelectionDynamic(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    slots: List<Triple<String, String, Boolean>>, // label, range, available
    selectedLabels: List<String>,
    onLabelToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(categories) { slot ->
                TimeSlotChip(
                    text = slot,
                    isSelected = slot == selectedCategory,
                    onClick = { onCategorySelected(slot) }
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val firstRow = slots.take(4)
            val secondRow = slots.drop(4)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(firstRow) { (label, _, available) ->
                    TimeSlotButton(
                        time = label,
                        isSelected = selectedLabels.contains(label),
                        isBooked = !available,
                        onClick = { onLabelToggle(label) }
                    )
                }
            }

            if (secondRow.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(secondRow) { (label, _, available) ->
                        TimeSlotButton(
                            time = label,
                            isSelected = selectedLabels.contains(label),
                            isBooked = !available,
                            onClick = { onLabelToggle(label) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AvailabilityIndicator(
                    color = Color(0xFF4CAF50),
                    text = "Available",
                    modifier = Modifier.weight(1f)
                )
                AvailabilityIndicator(
                    color = Color(0xFF00BCD4),
                    text = "Selected",
                    modifier = Modifier.weight(1f)
                )
                AvailabilityIndicator(
                    color = Color(0xFFE0E0E0),
                    text = "Booked",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TimeSlotChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
        border = if (!isSelected) BorderStroke(1.dp, Color.Gray) else null,
        modifier = Modifier.clickable { onClick() }
            ) {
                Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun TimeSlotButton(
    time: String,
    isSelected: Boolean,
    isBooked: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isBooked -> Color(0xFFE0E0E0)
        isSelected -> Color(0xFF00BCD4)
        else -> Color(0xFF4CAF50)
    }

    val textColor = when {
        isBooked -> Color.Gray
        else -> Color.White
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
                    modifier = Modifier
            .clickable(enabled = !isBooked) { onClick() }
            .width(90.dp)
            .height(40.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = time,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun AvailabilityIndicator(
    color: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
            Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun BookingBottomBar(
    price: String,
    duration: String,
    onProceedClick: () -> Unit
) {
    Column {
        Surface(
            color = Color(0xFFFF5722),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = "Offer",
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.padding(4.dp)
                    )
                }
                    Text(
                    text = "Offer applied! You are saving ₹200",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Surface(
            color = Color(0xFF4CAF50),
            modifier = Modifier.fillMaxWidth()
) {
    Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
                Column {
        Text(
                        text = price,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
        )
        Text(
                        text = duration,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }

    Button(
                    onClick = onProceedClick,
        colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
            Text(
                        text = "PROCEED",
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Proceed",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}



