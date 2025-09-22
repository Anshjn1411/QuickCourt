package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import android.os.Build
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.odoo_235.data.models.Court
import com.project.odoo_235.data.models.*
import com.project.odoo_235.data.models.Ratings
import com.project.odoo_235.data.models.TimeSlot
import com.project.odoo_235.ui.theme.AppColors
import com.project.odoo_235.ui.theme.AppColors1
import com.project.odoo_235.ui.theme.Odoo_235Theme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.map

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VenueDetailScreen(
    venueId: String,
    venueViewModel: BookingViewModel,
    navController: NavController,
    onGetDirectionClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val context = LocalContext.current
    val venue by venueViewModel.venueDetail.collectAsState()
    val loading by venueViewModel.loading.collectAsState()
    val error by venueViewModel.error.collectAsState()
    val bookingState by venueViewModel.bookingState.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val availability by venueViewModel.availability.collectAsState()
    val court: Court? = venue

    LaunchedEffect(venueId) {
        venueViewModel.resetBookingState()
        venueViewModel.fetchVenueDetail(venueId)
        venueViewModel.fetchAvailabilityForDate(venueId, selectedDate.toString())
    }

    LaunchedEffect(selectedDate, venueId) {
        venueViewModel.fetchAvailabilityForDate(venueId, selectedDate.toString())
    }

    // Loading or data not ready
    if (loading || court == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF4CAF50)
            )
        }
        return
    }

    // Error State
    if (error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error!!,
                    fontSize = 16.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        venueViewModel.fetchVenueDetail(venueId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
        return
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Image
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // Court Image - handle list of images
                    val imageUrl = if (court.images.isNotEmpty()) {
                        court.images.first().url
                    } else {
                        "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=800&q=80"
                    }

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = court.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )

                    // Rating Badge (Top Right)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {

                    }

                    // Page Indicators (Bottom Center) - only show if multiple images
                    if (court.images.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(court.images.size.coerceAtMost(3)) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            color = if (index == 0) Color.White else Color.White.copy(
                                                alpha = 0.5f
                                            ),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Court Name and Rating
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = court.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${court.location.city}, ${court.location.state}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    IconButton(onClick = { /* Handle favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.Gray
                        )
                    }
                }

                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${court.ratings.average}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = "(${court.ratings.totalReviews})",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
        // Get Direction and Call Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val lat = court.location.coordinates.lat
                        val lng = court.location.coordinates.lng
                        val label = Uri.encode(court.name)
                        val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            // Fallback to any maps-capable app or browser
                            val browserUri = Uri.parse("https://maps.google.com/?q=$lat,$lng")
                            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Get Direction",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),   // Background color
                        contentColor = Color.White            // Text and icon color
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Call",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Available Sports
        item {
            Text(
                text = "Available Sports",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                item {
                    SportChip(text = court.sportType, isSelected = true)
                }
                items(listOf("Cricket", "Basketball")) { sport ->
                    SportChip(text = sport, isSelected = false)
                }
            }
        }

        // Venue Info
        item {
            Column {
                Text(
                    text = "Venue Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                VenueInfoItem(
                    icon = Icons.Default.LocationOn,
                    title = "Pitch",
                    description = "${court.dimensions.length}x${court.dimensions.width} ${court.dimensions.unit}"
                )

                if (court.equipment.provided) {
                    VenueInfoItem(
                        icon = Icons.Default.Check,
                        title = "Equipment Provided",
                        description = court.equipment.items?.joinToString(", ") ?: ""
                    )
                }

                VenueInfoItem(
                    icon = Icons.Default.Grass,
                    title = "Surface Type",
                    description = court.surfaceType
                )
            }
        }

        // Amenities
        item {
            Text(
                text = "Amenities",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                court.amenities.forEach { amenity ->
                    AmenityItem(amenity = amenity)
                }

                if (court.lighting.available) {
                    AmenityItem(amenity = "${court.lighting.type} Lighting Available")
                }
            }
        }

        // Operating Hours
        item {
            Text(
                text = "Operating Hours",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OperatingHourItem("Monday - Friday", "06:00 AM - 10:00 PM")
                OperatingHourItem("Saturday - Sunday", "07:00 AM - 11:00 PM")
            }
        }

        // Rules
        item {
            Text(
                text = "Rules",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                court.rules.forEach { rule ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "• ",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = rule,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }

        // Book Now Button
        item {
            Button(
                onClick = {
                    navController.navigate("booking_select/${venueId}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors1.SportsDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "PROCEED TO SELECT A SLOT",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun SportChip(
    text: String,
    isSelected: Boolean
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFF5F5F5),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun VenueInfoItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(20.dp)
        )
        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AmenityItem(amenity: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Available",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = amenity,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun OperatingHourItem(
    days: String,
    hours: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = days,
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = hours,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Removed static sample data. All data now loads dynamically from API via BookingViewModel.



