package com.project.odoo_235.presentation.screens.user.screen.MianScreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.project.odoo_235.data.models.Court
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtBottomBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtSearchBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtTopBar
import com.project.odoo_235.ui.theme.AppColors
import kotlin.math.round
import com.project.odoo_235.R

@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    currentRoute: String = "Home"
) {
    val context = LocalContext.current

    val cityName by mainViewModel.currentCity.collectAsState()
    val courts by mainViewModel.courts.collectAsState()
    val isLoading by mainViewModel.loading.collectAsState()
    val errorMessage by mainViewModel.error.collectAsState()
    val searchQuery by mainViewModel.searchQuery.collectAsState()

    // Use backend ratings
    val ratingsMap = remember(courts) {
        courts.associate { it.id to it.ratings.average }
    }

    // Filter states
    var selectedCity by remember { mutableStateOf<String?>(null) }
    var selectedSport by remember { mutableStateOf<String?>(null) }
    var selectedRating by remember { mutableStateOf(1) }

    // Dialog control states - only for city and rating now
    var showCityDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }

    // Compute distinct options
    val cityOptions = courts.map { it.location.city }.distinct().sorted()
    val sportOptions = listOf("All Sports", "Cricket", "Football", "Tennis", "Basketball") // Static sport options

    // FILTER THE COURTS
    val filteredCourts = remember(courts, selectedCity, selectedSport, selectedRating) {
        courts.filter { court ->
            val ratingDouble = ratingsMap[court.id] ?: 0.0
            val rating = kotlin.math.round(ratingDouble).toInt()

            val cityMatches = selectedCity == null || court.location.city.equals(selectedCity, ignoreCase = true)
            val sportMatches = selectedSport == null || court.sportType.equals(selectedSport, ignoreCase = true)
            val ratingMatches = rating >= selectedRating

            cityMatches && sportMatches && ratingMatches
        }
    }

    val userName by mainViewModel.userName.collectAsState()
    val currentCity by mainViewModel.currentCity.collectAsState()
    val locationLoading by mainViewModel.locationLoading.collectAsState()
    LaunchedEffect(Unit) {
        mainViewModel.fetchCurrentCity(context)
        mainViewModel.fetchCourts()
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(paddingValues).padding(start = 5.dp, end = 5.dp)
        ) {
            // Fixed Search Bar at top
            var query by remember { mutableStateOf("") }
            QuickCourtSearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    mainViewModel.setSearchQuery(it)
                },
                onVoiceClick = { /* Handle voice search */ }
            )

            // Everything else in a scrollable LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Image Slider
                item {
                    ImageSlider()
                }

                // Sports List
                item {
                    SportsList(navController)
                }

                // Filter Section - now with inline sport selection
                item {
                    FilterSection(
                        selectedCity = selectedCity,
                        selectedSport = selectedSport,
                        selectedRating = selectedRating,
                        sportOptions = sportOptions,
                        onCityFilterClick = { showCityDialog = true },
                        onSportSelected = { sport ->
                            selectedSport = if (sport == "All Sports") null else sport
                        },
                        onRatingFilterClick = { showRatingDialog = true }
                    )
                }

                // Loading/Error/Content
                when {
                    isLoading -> {
                        item {
                            LoadingSection()
                        }
                    }
                    !errorMessage.isNullOrEmpty() -> {
                        item {
                            ErrorSection(errorMessage ?: "Unknown error")
                        }
                    }
                    else -> {
                        val q = searchQuery.trim()
                        val searched = if (q.isBlank()) filteredCourts else filteredCourts.filter { c ->
                            c.name.contains(q, ignoreCase = true) ||
                            c.location.city.contains(q, ignoreCase = true) ||
                            c.sportType.contains(q, ignoreCase = true)
                        }
                        if (searched.isEmpty()) {
                            item {
                                if (courts.isEmpty()) {
                                    EmptyStateSection("No courts available")
                                } else {
                                    EmptyStateSection("No courts match your filters")
                                }
                            }
                        } else {
                            // Courts List Items - each court as separate item for better performance
                            items(searched, key = { it.id }) { court ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
                                ) {
                                    ModernCourtCard(
                                        court = court,
                                        rating = ratingsMap[court.id] ?: 0.0,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        navController.navigate(Screen.BookingScreen.bookingScreen(court.id))
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Only City and Rating Dialogs now
    if (showCityDialog) {
        EnhancedFilterDialog(
            title = "Select City",
            options = listOf("All Cities") + cityOptions,
            selectedOption = selectedCity ?: "All Cities",
            onDismiss = { showCityDialog = false },
            onOptionSelected = { selected ->
                selectedCity = if (selected == "All Cities") null else selected
                showCityDialog = false
            }
        )
    }

    if (showRatingDialog) {
        EnhancedFilterDialog(
            title = "Select Minimum Rating",
            options = listOf("1⭐", "2⭐", "3⭐", "4⭐", "5⭐"),
            selectedOption = "$selectedRating⭐",
            onDismiss = { showRatingDialog = false },
            onOptionSelected = { selected ->
                selectedRating = selected.first().digitToIntOrNull() ?: 1
                showRatingDialog = false
            }
        )
    }
}

// Updated FilterSection with inline sport selection
@Composable
fun FilterSection(
    selectedCity: String?,
    selectedSport: String?,
    selectedRating: Int,
    sportOptions: List<String>,
    onCityFilterClick: () -> Unit,
    onSportSelected: (String) -> Unit,
    onRatingFilterClick: () -> Unit
) {
    Column {
        Spacer(Modifier.height(5.dp))
        Text("Available Venues",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(5.dp))
        // Sport Filter Row - inline selection
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(sportOptions) { sport ->
                SportFilterChip(
                    label = sport,
                    isSelected = when {
                        sport == "All Sports" && selectedSport == null -> true
                        sport == selectedSport -> true
                        else -> false
                    },
                    onClick = { onSportSelected(sport) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // City and Rating Filter Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // City Filter
            item {
                SportFilterChip(
                    label = selectedCity ?: "All Cities",
                    isSelected = selectedCity != null,
                    onClick = onCityFilterClick
                )
            }

            // Rating Filter
            item {
                SportFilterChip(
                    label = if (selectedRating > 1) "$selectedRating⭐+" else "Any Rating",
                    isSelected = selectedRating > 1,
                    onClick = onRatingFilterClick
                )
            }
        }
    }
}

// Updated ModernCourtCard with modifier support
@Composable
fun ModernCourtCard(
    court: Court,
    rating: Double,
    modifier: Modifier = Modifier,
    onBookClick: () -> Unit
) {
    // Bookable if available + active + approved
    val canBook = court.availability.isAvailable &&
            (court.status == "active") &&
            (court.approvalStatus == "approved")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onBookClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Image Section with Overlay Content
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
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", rating),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Heart Icon (Top Right, next to rating)
                IconButton(
                    onClick = { /* Handle favorite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 80.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
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
                                        color = if (index == 0) Color.White else Color.White.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title and Location
                Text(
                    text = court.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${court.location.city}, ${court.location.state}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sports and Amenities Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sports Icon
                    Icon(
                        imageVector = when(court.sportType.lowercase()) {
                            "football" -> Icons.Default.SportsFootball
                            "cricket" -> Icons.Default.SportsCricket
                            "tennis" -> Icons.Default.SportsTennis
                            "basketball" -> Icons.Default.SportsBasketball
                            else -> Icons.Default.SportsHandball
                        },
                        contentDescription = court.sportType,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Show amenities instead of hardcoded sports
                    Text(
                        text = court.amenities.take(3).joinToString(" • "),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Amenities Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Calendar/Booking Icon
                        if (canBook) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = "Bookable",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Lighting Icon
                        if (court.lighting.available) {
                            Icon(
                                imageVector = Icons.Outlined.WbSunny,
                                contentDescription = "Lighting Available",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Rest of the components remain the same...
@Composable
fun SportsList(navController: NavController) {
    Column(

    ) {
        Text("Sports",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            SportsCard(
                title = "Football",
                titleColor = Color(0xFFFF6F00),
                backgroundColor = Color(0xFFFFE0B2),
                imageRes = R.drawable.football,
                modifier = Modifier.weight(1f)
            )

            SportsCard(
                title = "Cricket",
                titleColor = Color(0xFF2E7D32),
                backgroundColor = Color(0xFFC8E6C9),
                imageRes = R.drawable.cricket,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.CricketMatchList.routes) }
            )

            SportsCard(
                title = "Basketball",
                titleColor = Color(0xFF4527A0),
                backgroundColor = Color(0xFFD1C4E9),
                imageRes = R.drawable.basketball,
                modifier = Modifier.weight(1f)
            )

            SportsCard(
                title = "Badminton",
                titleColor = Color(0xFF0277BD),
                backgroundColor = Color(0xFFB3E5FC),
                imageRes = R.drawable.badminton,
                modifier = Modifier.weight(1f)
            )
        }

    }

}

@Composable
fun SportsCard(
    title: String,
    titleColor: Color,
    backgroundColor: Color,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick?.invoke() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = titleColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SportFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected && label == "All Sports") {
                    Color(0xFF4CAF50) // Green color for "All Sports" when selected
                } else if (isSelected) {
                    AppColors.Primary.copy(alpha = 0.1f)
                } else {
                    Color(0xFFF5F5F5) // Light gray background
                },
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = if (isSelected && label != "All Sports") 1.dp else 0.dp,
                color = if (isSelected) AppColors.Primary else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = when {
                isSelected && label == "All Sports" -> Color.White
                isSelected -> AppColors.Primary
                else -> Color(0xFF757575)
            },
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = AppColors.Primary,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading courts...",
                color = AppColors.OnSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ErrorSection(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Error.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = AppColors.Error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Something went wrong",
                fontWeight = FontWeight.Bold,
                color = AppColors.Error
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EmptyStateSection(message: String = "No courts found") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedFilterDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        },
        text = {
            LazyColumn {
                items(options) { option ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable { onOptionSelected(option) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (option == selectedOption)
                                AppColors.Primary.copy(alpha = 0.1f)
                            else
                                Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            RadioButton(
                                selected = option == selectedOption,
                                onClick = { onOptionSelected(option) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = AppColors.Primary
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = option,
                                color = if (option == selectedOption) AppColors.Primary else AppColors.OnSurface,
                                fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AppColors.Primary
                )
            ) {
                Text("Close")
            }
        },
        containerColor = AppColors.Surface,
        shape = RoundedCornerShape(16.dp)
    )
}