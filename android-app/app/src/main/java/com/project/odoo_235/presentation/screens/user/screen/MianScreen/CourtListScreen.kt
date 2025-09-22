package com.project.odoo_235.presentation.screens.user.screen.MianScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.odoo_235.data.models.Court
import com.project.odoo_235.data.models.CourtsListPagination
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtBottomBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtSearchBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtTopBar
import com.project.odoo_235.ui.theme.AppColors

@Composable
fun CourtsListScreen(navController: NavController, mainViewModel: MainViewModel , currentRoute: String = "Court") {
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
    val filteredCourts = remember(courts, selectedCity, selectedSport, selectedRating, searchQuery) {
        courts.filter { court ->
            val ratingDouble = ratingsMap[court.id] ?: 0.0
            val rating = kotlin.math.round(ratingDouble).toInt()

            val cityMatches = selectedCity == null || court.location.city.equals(selectedCity, ignoreCase = true)
            val sportMatches = selectedSport == null || court.sportType.equals(selectedSport, ignoreCase = true)
            val ratingMatches = rating >= selectedRating
            
            // Search query filter
            val searchMatches = searchQuery.trim().isBlank() || 
                court.name.contains(searchQuery.trim(), ignoreCase = true) ||
                court.location.city.contains(searchQuery.trim(), ignoreCase = true) ||
                court.sportType.contains(searchQuery.trim(), ignoreCase = true)

            cityMatches && sportMatches && ratingMatches && searchMatches
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
            QuickCourtSearchBar(
                query = searchQuery,
                onQueryChange = { mainViewModel.setSearchQuery(it) },
                onVoiceClick = { /* Handle voice search */ }
            )

            // Everything else in a scrollable LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        if (filteredCourts.isEmpty()) {
                            item {
                                if (courts.isEmpty()) {
                                    EmptyStateSection("No courts available")
                                } else {
                                    EmptyStateSection("No courts match your filters")
                                }
                            }
                        } else {
                            // Courts List Items - each court as separate item for better performance
                            items(filteredCourts, key = { it.id }) { court ->
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
