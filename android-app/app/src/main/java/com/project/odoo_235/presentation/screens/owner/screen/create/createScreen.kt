package com.project.odoo_235.presentation.screens.owner.create

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.ui.theme.AppColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourtScreen(
    createCourtViewModel: CreateCourtViewModel = viewModel(),
    onCourtCreated: () -> Unit,
    onBackPressed: () -> Unit = {}
) {

    val context = LocalContext.current

    var venueId by remember { mutableStateOf("") }
    val sessionManager = remember { UserSessionManager(context) }

    // Form fields
    var venueName by remember { mutableStateOf("") }
    var courtName by remember { mutableStateOf("") }
    var availableDate by remember { mutableStateOf("") }
    var availableTime by remember { mutableStateOf("") }

    // UI state
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }
    val loading by createCourtViewModel.loading.collectAsState()
    val error by createCourtViewModel.error.collectAsState()
    val successMessage by createCourtViewModel.successMessage.collectAsState()


    // Required fields
    var name by remember { mutableStateOf("") }
    var sportType by remember { mutableStateOf("") }
    var surfaceType by remember { mutableStateOf("Synthetic") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("India") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var basePrice by remember { mutableStateOf("") }

    var currency by remember { mutableStateOf("INR") }
    var hourlyRate by remember { mutableStateOf(true) }
    var minPlayers by remember { mutableStateOf("") }
    var maxPlayers by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("meters") }

    // Optional flags
    var lightingAvailable by remember { mutableStateOf(false) }
    var lightingType by remember { mutableStateOf<String?>(null) }
    var lightingAdditionalCost by remember { mutableStateOf<String?>(null) }
    var equipmentProvided by remember { mutableStateOf(false) }
    var equipmentItemsJson by remember { mutableStateOf<String?>(null) }
    var equipmentRentalCost by remember { mutableStateOf<String?>(null) }
    var isAvailable by remember { mutableStateOf(true) }
    var maintenanceMode by remember { mutableStateOf(false) }
    var maintenanceReason by remember { mutableStateOf<String?>(null) }
    var maintenanceEndDate by remember { mutableStateOf<String?>(null) }
    var operatingHoursJson by remember { mutableStateOf<String?>(null) }
    val amenities = remember { mutableStateListOf<String>() }
    val rules = remember { mutableStateListOf<String>() }
    val images = remember { mutableStateListOf<Uri>() } // bind with image picker

    val cachedUser by sessionManager.userData.collectAsState(initial = null)
    LaunchedEffect(cachedUser) {
        cachedUser?.let {
            venueId = it.id
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            showSuccessAnimation = true
            delay(2000)
            onCourtCreated()
        }
    }

    // Predefined options for better UX
    val sportTypes =
        listOf("Basketball", "Tennis", "Football", "Badminton", "Cricket", "Volleyball")
    val timeSlots = listOf(
        "6:00 AM - 8:00 AM",
        "8:00 AM - 10:00 AM",
        "10:00 AM - 12:00 PM",
        "12:00 PM - 2:00 PM",
        "2:00 PM - 4:00 PM",
        "4:00 PM - 6:00 PM",
        "6:00 PM - 8:00 PM",
        "8:00 PM - 10:00 PM"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // Custom Top Bar
            CreateCourtTopBar(
                onBackPressed = onBackPressed,
                title = "Create New Court"
            )

            // Success Animation Overlay
            AnimatedVisibility(
                visible = showSuccessAnimation,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SuccessAnimationOverlay()
            }

            // Main Form Content
            AnimatedVisibility(
                visible = !showSuccessAnimation,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Progress Header
                    CreateCourtHeader(currentStep = currentStep, totalSteps = 3)

                    // Form Sections
                    VenueInfoSection(
                        venueName = venueName,
                        onVenueNameChange = { venueName = it },
                        courtName = courtName,
                        onCourtNameChange = { courtName = it },
                        city = city,
                        onCityChange = { city = it }
                    )

                    SportTypeSection(
                        sportTypes = sportTypes,
                        selectedSport = sportType,
                        onSportSelected = { sportType = it }
                    )

                    SchedulingSection(
                        availableDate = availableDate,
                        onDateChange = { availableDate = it },
                        timeSlots = timeSlots,
                        selectedTime = availableTime,
                        onTimeSelected = { availableTime = it }
                    )

                    // Error Display
                    AnimatedVisibility(
                        visible = error != null,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        ErrorCard(error = error ?: "")
                    }

                    Button(
                        onClick = {
                            if (name.isBlank() || sportType.isBlank() || surfaceType.isBlank() ||
                                description.isBlank() || address.isBlank() || city.isBlank() ||
                                state.isBlank() || zipCode.isBlank() || latitude.isBlank() ||
                                longitude.isBlank() || basePrice.isBlank() || minPlayers.isBlank() ||
                                maxPlayers.isBlank() || length.isBlank() || width.isBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Please fill all required fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            createCourtViewModel.createCourt(
                                context = context,
                                name = name,
                                sportType = sportType,
                                surfaceType = surfaceType,
                                description = description,
                                address = address,
                                city = city,
                                state = state,
                                zipCode = zipCode,
                                country = country,
                                latitude = latitude,
                                longitude = longitude,
                                basePrice = basePrice,
                                currency = currency,
                                hourlyRate = hourlyRate,
                                minPlayers = minPlayers,
                                maxPlayers = maxPlayers,
                                length = length,
                                width = width,
                                unit = unit,
                                lightingAvailable = lightingAvailable,
                                lightingType = lightingType,
                                lightingAdditionalCost = lightingAdditionalCost,
                                equipmentProvided = equipmentProvided,
                                equipmentItemsJson = equipmentItemsJson,
                                equipmentRentalCost = equipmentRentalCost,
                                isAvailable = isAvailable,
                                maintenanceMode = maintenanceMode,
                                maintenanceReason = maintenanceReason,
                                maintenanceEndDate = maintenanceEndDate,
                                operatingHoursJson = operatingHoursJson,
                                amenities = amenities.toList(),
                                rules = rules.toList(),
                                images = images.toList()
                            )
                        },
                        enabled = !loading
                    ) {
                        Text(if (loading) "Creating..." else "Create Court")
                    }

                    if (error != null) {
                        Text(error ?: "", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}


@Composable
fun CreateCourtTopBar(
    onBackPressed: () -> Unit,
    title: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .background(AppColors.Primary.copy(0.1f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = AppColors.Primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )
        }
    }
}

@Composable
fun CreateCourtHeader(currentStep: Int, totalSteps: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(AppColors.Primary, AppColors.Primary.copy(0.8f))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Let's create your court! 🏆",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fill in the details below to add a new court to your facility",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.9f)
                )
            }
        }
    }
}

@Composable
fun VenueInfoSection(
    venueName: String,
    onVenueNameChange: (String) -> Unit,
    courtName: String,
    onCourtNameChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit
) {
    FormSection(
        title = "Venue Information",
        icon = Icons.Default.Store,
        color = Color(0xFF4CAF50)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EnhancedTextField(
                value = venueName,
                onValueChange = onVenueNameChange,
                label = "Venue Name",
                placeholder = "e.g., Sports Complex XYZ",
                leadingIcon = Icons.Default.Store
            )

            EnhancedTextField(
                value = courtName,
                onValueChange = onCourtNameChange,
                label = "Court Name",
                placeholder = "e.g., Court A, Center Court",
                leadingIcon = Icons.Default.SportsTennis
            )

            EnhancedTextField(
                value = city,
                onValueChange = onCityChange,
                label = "City",
                placeholder = "e.g., Mumbai, Delhi",
                leadingIcon = Icons.Default.LocationOn
            )
        }
    }
}

@Composable
fun SportTypeSection(
    sportTypes: List<String>,
    selectedSport: String,
    onSportSelected: (String) -> Unit
) {
    FormSection(
        title = "Sport Type",
        icon = Icons.Default.SportsTennis,
        color = Color(0xFF2196F3)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(180.dp)
        ) {
            items(sportTypes.size) { index ->
                val sport = sportTypes[index]
                SportTypeCard(
                    sport = sport,
                    isSelected = selectedSport == sport,
                    onSelected = { onSportSelected(sport) }
                )
            }
        }
    }
}

@Composable
fun SportTypeCard(
    sport: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .clickable { onSelected() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) AppColors.Primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppColors.Primary.copy(0.1f) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = sport,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) AppColors.Primary else AppColors.OnSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SchedulingSection(
    availableDate: String,
    onDateChange: (String) -> Unit,
    timeSlots: List<String>,
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    FormSection(
        title = "Scheduling",
        icon = Icons.Default.Schedule,
        color = Color(0xFFFF9800)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EnhancedTextField(
                value = availableDate,
                onValueChange = onDateChange,
                label = "Available Date",
                placeholder = "YYYY-MM-DD",
                leadingIcon = Icons.Default.CalendarToday,
                keyboardType = KeyboardType.Number
            )

            Text(
                text = "Available Time Slots",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(timeSlots.size) { index ->
                    val timeSlot = timeSlots[index]
                    TimeSlotCard(
                        timeSlot = timeSlot,
                        isSelected = selectedTime == timeSlot,
                        onSelected = { onTimeSelected(timeSlot) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeSlotCard(
    timeSlot: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) AppColors.Primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppColors.Primary.copy(0.1f) else Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = timeSlot,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) AppColors.Primary else AppColors.OnSurface,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color.copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
            }

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = AppColors.Primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Primary,
            focusedLabelColor = AppColors.Primary,
            cursorColor = AppColors.Primary
        ),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun ErrorCard(error: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check, // You might want to use an error icon
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun CreateCourtButton(
    loading: Boolean,
    enabled: Boolean,
    onCreateCourt: () -> Unit
) {
    Button(
        onClick = onCreateCourt,
        enabled = enabled && !loading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary,
            disabledContainerColor = AppColors.Primary.copy(0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Creating Court...",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Create Court",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SuccessAnimationOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AppColors.Primary, AppColors.Primary.copy(0.7f))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Court Created Successfully! 🎉",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.Primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your new court has been added to your facility",
                style = MaterialTheme.typography.bodyLarge,
                color = AppColors.OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun isFormValid(
    venueName: String,
    sportType: String,
    courtName: String,
    city: String,
    availableDate: String,
    availableTime: String
): Boolean {
    return venueName.isNotBlank() &&
            sportType.isNotBlank() &&
            courtName.isNotBlank() &&
            city.isNotBlank() &&
            availableDate.isNotBlank() &&
            availableTime.isNotBlank()
}

// LazyVerticalGrid implementation for Compose compatibility
@Composable
fun LazyVerticalGrid(
    columns: GridCells,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: LazyGridScope.() -> Unit
) {
    // For simplicity, this is a very basic grid implementation using Column + Rows.
    // Note: This is not as efficient as official LazyVerticalGrid from foundation-layout.

    val gridScope = object : LazyGridScope {
        private val itemsList = mutableListOf<@Composable () -> Unit>()

        override fun items(
            count: Int,
            key: ((index: Int) -> Any)?,
            itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
        ) {
            repeat(count) { index ->
                itemsList.add {
                    itemContent(object : LazyGridItemScope {}, index)
                }
            }
        }

        fun getItems() = itemsList.toList()
    }

    // Build the content inside the custom grid scope
    gridScope.content()

    // Number of columns (only supports fixed count here)
    val columnCount = when (columns) {
        is GridCells.Fixed -> columns.count
        else -> 1 // fallback
    }

    // Arrange items in rows inside the column
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
    ) {
        // Chunk the items by the column count to create rows
        gridScope.getItems().chunked(columnCount).forEach { rowItems ->
            Row(
                horizontalArrangement = horizontalArrangement,
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { itemComposable ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        itemComposable()
                    }
                }
                // If last row has less items than columns, fill the space with empty Boxes
                if (rowItems.size < columnCount) {
                    repeat(columnCount - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

interface LazyGridScope {
    fun items(
        count: Int,
        key: ((index: Int) -> Any)? = null,
        itemContent: @Composable LazyGridItemScope.(index: Int) -> Unit
    )
}

interface LazyGridItemScope

sealed class GridCells {
    data class Fixed(val count: Int) : GridCells()
}


