//package com.project.odoo_235.presentation.screens.googlemapscreen
//
//import android.Manifest
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.maps.android.compose.*
//import com.project.odoo_235.data.models.SportsVenue
//import com.project.odoo_235.presentation.navigation.Screen
//import com.project.odoo_235.presentation.screens.components.QuickCourtBottomBar
//import com.project.odoo_235.presentation.screens.googlemapscreen.LocationViewModel
//import com.project.odoo_235.ui.theme.AppColors
//
//data class FilterType(
//    val name: String,
//    val displayName: String,
//    val icon: ImageVector,
//    val color: Color
//)
//
//@Composable
//fun MapScreen(
//    navController: NavController,
//    locationViewModel: LocationViewModel = viewModel()
//) {
//    var hasLocationPermission by remember { mutableStateOf(false) }
//    val locationPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        hasLocationPermission = granted
//        if (granted) locationViewModel.fetchUserLocation()
//    }
//
//    LaunchedEffect(Unit) {
//        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//    }
//
//    val userLocation by locationViewModel.userLocation.collectAsState()
//
//    // Enhanced filter types with icons and colors
//    val filterTypes = listOf(
//        FilterType("All", "All Venues", Icons.Outlined.Map, AppColors.Primary),
//        FilterType("turf", "Turf", Icons.Outlined.Grass, AppColors.Success),
//        FilterType("cricket", "Cricket", Icons.Outlined.SportsBaseball, AppColors.Info),
//        FilterType("pickleball", "Pickleball", Icons.Outlined.SportsTennis, AppColors.Warning),
//        FilterType("academy", "Academy", Icons.Outlined.School, AppColors.Secondary),
//        FilterType("badminton", "Badminton", Icons.Outlined.SportsBaseball, AppColors.Error),
//        FilterType("football", "Football", Icons.Outlined.SportsSoccer, AppColors.Success),
//        FilterType("basketball", "Basketball", Icons.Outlined.SportsBasketball, AppColors.Primary),
//        FilterType("volleyball", "Volleyball", Icons.Outlined.SportsVolleyball, AppColors.Info),
//        FilterType("tennis", "Tennis", Icons.Outlined.SportsTennis, AppColors.Warning),
//        FilterType("sports", "Sports", Icons.Outlined.FitnessCenter, AppColors.Secondary)
//    )
//
//    val sportsVenues = remember {
//        listOf(
//            SportsVenue("The Turf Arena Box Cricket and Football", LatLng(23.2002494, 72.6087675), "turf"),
//            SportsVenue("Smash turf", LatLng(23.131794, 72.563293), "turf"),
//            SportsVenue("Elite Sports 1.0 Box cricket", LatLng(23.1149944, 72.6342708), "cricket"),
//            SportsVenue("STRIKERS SPORTS | BOX CRICKET & FOOTBALL TURF", LatLng(23.1109277, 72.6721158), "turf"),
//            SportsVenue("Power Play Cricket Turf", LatLng(23.1508067, 72.6054575), "cricket"),
//            SportsVenue("Elite sports 2.0 ( Box cricket / Football & Pickle ball )", LatLng(23.1088934, 72.6098042), "turf"),
//            SportsVenue("ICONIC TURF VAVOL", LatLng(23.2324993, 72.6049133), "turf"),
//            SportsVenue("Patel Brothers Box Cricket", LatLng(23.1416146, 72.5673583), "cricket"),
//            SportsVenue("RWorld Arena Cricket & Football Turf", LatLng(23.18104, 72.5650949), "turf"),
//            SportsVenue("7 King Cricket Box Arena", LatLng(23.1479693, 72.538244), "cricket"),
//            SportsVenue("Matrix 360 Box Cricket & Sports Club", LatLng(23.1845198, 72.6728595), "cricket"),
//            SportsVenue("3Ace Box Cricket and Pickleball", LatLng(23.1296698, 72.573224), "pickleball"),
//            SportsVenue("Phoenix Turf - Football, Box Cricket, Volleyball, Kabaddi and Hockey Booking", LatLng(23.1043888, 72.6140358), "turf"),
//            SportsVenue("ONESTOP TURF BOX CRICKET", LatLng(23.1842594, 72.5550697), "cricket"),
//            SportsVenue("All Stars Box Turf and Cafe", LatLng(23.1545866, 72.6013852), "turf"),
//            SportsVenue("Swing Sports", LatLng(23.1611112, 72.5961414), "turf"),
//            SportsVenue("Cric Bees Box Arena", LatLng(23.1113557, 72.502195), "cricket"),
//            SportsVenue("Java Sports Academy", LatLng(23.1276013, 72.5723759), "academy"),
//            SportsVenue("Shivay, The Cricketing Hub, Karai", LatLng(23.133662, 72.6583818), "cricket"),
//            SportsVenue("SGVP Cricket Ground", LatLng(23.1311893, 72.5384175), "cricket"),
//            SportsVenue("Malaviya Cricket Ground ONGC", LatLng(23.1031503, 72.5886893), "cricket"),
//            SportsVenue("Green Oval", LatLng(23.1522954, 72.6603885), "cricket"),
//            SportsVenue("Huddle Arena", LatLng(23.1019545, 72.6047873), "turf"),
//            SportsVenue("Play Ground - Sector 3 new", LatLng(23.2007078, 72.6276216), "playground"),
//            SportsVenue("Box warriors & Bachelor's Cafe", LatLng(23.164194, 72.661194), "cricket"),
//            SportsVenue("Champions Arena (Pickleball / Cricket / Football)", LatLng(23.1357766, 72.5398535), "pickleball"),
//            SportsVenue("DAIICT Football/Cricket Ground", LatLng(23.1901319, 72.6269319), "football"),
//            SportsVenue("All Season Box", LatLng(23.1590982, 72.6469506), "cricket"),
//            SportsVenue("ONGC Badminton Court", LatLng(23.1030263, 72.5838295), "badminton"),
//            SportsVenue("Phoenix badminton academy", LatLng(23.1042686, 72.613568), "badminton"),
//            SportsVenue("Savvy Swaraaj Sports Club", LatLng(23.1044849, 72.5493849), "sports"),
//            SportsVenue("Phoenix Sports Academy", LatLng(23.1042786, 72.6137733), "academy"),
//            SportsVenue("Dk badminton academy", LatLng(23.1087548, 72.5916926), "badminton"),
//            SportsVenue("H3 Sports Academy", LatLng(23.1273275, 72.5652159), "academy"),
//            SportsVenue("DA-IICT Badminton Courts", LatLng(23.1893004, 72.6265453), "badminton"),
//            SportsVenue("Aloka Sports Academy", LatLng(23.1888688, 72.7082972), "academy"),
//            SportsVenue("Lakshya Sports Academy", LatLng(23.1188462, 72.5566959), "academy"),
//            SportsVenue("VGEC Gymkhana", LatLng(23.1068136, 72.5943028), "sports"),
//            SportsVenue("NCCMA Club North", LatLng(23.1155553, 72.5560642), "sports"),
//            SportsVenue("Sai Sports Training Centre", LatLng(23.2342665, 72.6343831), "academy"),
//            SportsVenue("Elite Pickle Ball Motera", LatLng(23.1089653, 72.6096741), "pickleball"),
//            SportsVenue("Pickleball Legends", LatLng(23.1646875, 72.5649375), "pickleball"),
//            SportsVenue("Akshar Sports Academy", LatLng(23.1248432, 72.5667988), "academy"),
//            SportsVenue("IIT Gandhinagar Sports Complex", LatLng(23.211442, 72.689014), "sports"),
//            SportsVenue("Tennis court", LatLng(23.1215325, 72.6013714), "tennis"),
//            SportsVenue("Shree Balaji Agora Residency - Basket Ball Court", LatLng(23.1184059, 72.624874), "basketball"),
//            SportsVenue("IIT Gandhinagar New Basketball Court", LatLng(23.2113257, 72.6845779), "basketball"),
//            SportsVenue("IIT Gandhinagar Basketball Court 2", LatLng(23.211324, 72.6835998), "basketball"),
//            SportsVenue("DA-IICT Basketball COurt", LatLng(23.1907316, 72.626433), "basketball"),
//            SportsVenue("Basketball court", LatLng(23.1897855, 72.5789458), "basketball"),
//            SportsVenue("St. Xavier's School Basketball Ground", LatLng(23.2102399, 72.6498665), "basketball"),
//            SportsVenue("Cricket Ground, Nirma Vidyavihar", LatLng(23.1278831, 72.5467841), "cricket"),
//            SportsVenue("Drona Sports Academy", LatLng(23.1315435, 72.6370672), "academy"),
//            SportsVenue("I.C.T.A TENNIS ACADEMY", LatLng(23.1950197, 72.6366501), "tennis"),
//            SportsVenue("CRPF BASKETBALL COURT", LatLng(23.2526111, 72.6940434), "basketball"),
//            SportsVenue("Monic Chowk", LatLng(23.154153, 72.6602805), "playground"),
//            SportsVenue("Ace Bounce Pickleball", LatLng(23.1713278, 72.6343023), "pickleball"),
//            SportsVenue("𝗙𝗟𝗜𝗖𝗞 '𝗡 𝗥𝗢𝗟𝗟 | 𝗕𝗲𝘀𝘁 𝗣𝗶𝗰𝗸𝗹𝗲𝗯𝗮𝗹𝗹 𝗜𝗻 𝗚𝗮𝗻𝗱𝗵𝗶𝗻𝗮𝗴𝗮𝗿", LatLng(23.208178, 72.6542179), "pickleball"),
//            SportsVenue("PICKLE HUB", LatLng(23.1333151, 72.5411123), "pickleball"),
//            SportsVenue("Pickle Park", LatLng(23.1228897, 72.6343009), "pickleball"),
//            SportsVenue("Thunder box cricket & pickle ball", LatLng(23.1118806, 72.6082814), "pickleball"),
//            SportsVenue("SwingZone Box Cricket & Pickle Ball", LatLng(23.106312, 72.5532635), "pickleball"),
//            SportsVenue("Zodiac Cricket Ground By Point Red Sports Club", LatLng(23.1392468, 72.5500499), "cricket"),
//            SportsVenue("Pramukh Sports Ground", LatLng(23.147414, 72.5892321), "sports"),
//            SportsVenue("Sports Club- Vollyball Ground", LatLng(23.2116169, 72.6214598), "volleyball"),
//            SportsVenue("GROUND 83", LatLng(23.1482261, 72.5259613), "sports"),
//            SportsVenue("SwingZone 2 Box Cricket", LatLng(23.1315722, 72.5749503), "cricket"),
//            SportsVenue("Champion Sports Academy", LatLng(23.1139517, 72.5762056), "academy"),
//            SportsVenue("Smashboundary Box Cricket & volleyball", LatLng(23.1680067, 72.6067987), "volleyball"),
//            SportsVenue("Umiya Cricket Ground", LatLng(23.1307406, 72.5229728), "cricket"),
//            SportsVenue("Dadobat Sports & Cafe | Cricket Box | Science City", LatLng(23.1012162, 72.5039702), "cricket"),
//            SportsVenue("The Seven Sky Box Cricket", LatLng(23.1182613, 72.6122676), "cricket")
//        )
//    }
//
//    var selectedType by remember { mutableStateOf("All") }
//    var showVenuesList by remember { mutableStateOf(false) }
//
//    // Filter the venues based on selectedType
//    val filteredVenues = remember(selectedType) {
//        if (selectedType == "All") sportsVenues
//        else sportsVenues.filter { it.type == selectedType }
//    }
//    Scaffold(
//        modifier = Modifier.padding(top = 16.dp),
//        bottomBar = {
//            QuickCourtBottomBar(navController, Screen.search.routes)
//        }
//    ) {
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(AppColors.Background).padding(it)
//        ) {
//            // Enhanced Header
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
//                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(
//                            brush = Brush.linearGradient(
//                                colors = listOf(AppColors.Primary, AppColors.PrimaryVariant)
//                            )
//                        )
//                        .padding(16.dp)
//                ) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column {
//                            Text(
//                                text = "Find Sports Venues",
//                                fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                                color = AppColors.OnPrimary
//                            )
//                            Text(
//                                text = "Gandhinagar • ${filteredVenues.size} venues found",
//                                fontSize = 12.sp,
//                                color = AppColors.OnPrimary.copy(alpha = 0.8f)
//                            )
//                        }
//
//                        Row(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            // Toggle List View
//                            IconButton(
//                                onClick = { showVenuesList = !showVenuesList },
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.White.copy(alpha = 0.2f))
//                            ) {
//                                Icon(
//                                    imageVector = if (showVenuesList) Icons.Outlined.Map else Icons.Outlined.List,
//                                    contentDescription = "Toggle view",
//                                    tint = AppColors.OnPrimary,
//                                    modifier = Modifier.size(20.dp)
//                                )
//                            }
//
//                            // Profile Button
//                            IconButton(
//                                onClick = { navController.navigate("profile") },
//                                modifier = Modifier
//                                    .size(40.dp)
//                                    .clip(CircleShape)
//                                    .background(Color.White.copy(alpha = 0.2f))
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Outlined.Person,
//                                    contentDescription = "Profile",
//                                    tint = AppColors.OnPrimary,
//                                    modifier = Modifier.size(20.dp)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            // Enhanced Filter Section
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
//                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//            ) {
//                LazyRow(
//                    modifier = Modifier.padding(12.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    items(filterTypes) { filterType ->
//                        FilterChip(
//                            filterType = filterType,
//                            isSelected = selectedType == filterType.name,
//                            onClick = { selectedType = filterType.name }
//                        )
//                    }
//                }
//            }
//
//            // Main Content
//            Box(modifier = Modifier.weight(1f)) {
//                when {
//                    !hasLocationPermission -> {
//                        // Permission Required State
//                        Card(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(16.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(24.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Outlined.LocationOff,
//                                    contentDescription = "Location permission required",
//                                    modifier = Modifier.size(64.dp),
//                                    tint = AppColors.OnSurfaceVariant
//                                )
//                                Spacer(modifier = Modifier.height(16.dp))
//                                Text(
//                                    text = "Location Permission Required",
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.SemiBold,
//                                    color = AppColors.OnSurface,
//                                    textAlign = TextAlign.Center
//                                )
//                                Text(
//                                    text = "We need location access to show nearby sports venues",
//                                    fontSize = 14.sp,
//                                    color = AppColors.OnSurfaceVariant,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.padding(top = 8.dp)
//                                )
//                                Spacer(modifier = Modifier.height(24.dp))
//                                Button(
//                                    onClick = {
//                                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                                    },
//                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
//                                    shape = RoundedCornerShape(12.dp)
//                                ) {
//                                    Text("Grant Permission", color = AppColors.OnPrimary)
//                                }
//                            }
//                        }
//                    }
//
//                    userLocation == null -> {
//                        // Loading State
//                        Card(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(16.dp),
//                            shape = RoundedCornerShape(16.dp),
//                            colors = CardDefaults.cardColors(containerColor = AppColors.Surface)
//                        ) {
//                            Box(
//                                modifier = Modifier.fillMaxSize(),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Column(
//                                    horizontalAlignment = Alignment.CenterHorizontally
//                                ) {
//                                    CircularProgressIndicator(
//                                        color = AppColors.Primary,
//                                        strokeWidth = 3.dp
//                                    )
//                                    Spacer(modifier = Modifier.height(16.dp))
//                                    Text(
//                                        text = "Finding your location...",
//                                        fontSize = 14.sp,
//                                        color = AppColors.OnSurfaceVariant
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    else -> {
//                        // Map View
//                        val cameraPositionState = rememberCameraPositionState {
//                            position = CameraPosition.fromLatLngZoom(userLocation!!, 14f)
//                        }
//
//                        GoogleMap(
//                            modifier = Modifier.fillMaxSize(),
//                            cameraPositionState = cameraPositionState,
//                            uiSettings = MapUiSettings(
//                                zoomControlsEnabled = false,
//                                compassEnabled = true,
//                                myLocationButtonEnabled = true
//                            )
//                        ) {
//                            // User location marker
//                            Marker(
//                                state = MarkerState(position = userLocation!!),
//                                title = "You are here",
//                                snippet = "Your current location"
//                            )
//
//                            // Venue markers
//                            filteredVenues.forEach { venue ->
//                                Marker(
//                                    state = MarkerState(position = venue.location),
//                                    title = venue.name,
//                                    snippet = venue.type.uppercase()
//                                )
//                            }
//                        }
//
//                        // Floating Action Buttons
//                        Column(
//                            modifier = Modifier
//                                .align(Alignment.BottomEnd)
//                                .padding(16.dp),
//                            verticalArrangement = Arrangement.spacedBy(12.dp)
//                        ) {
//                            // My Location FAB
//                            FloatingActionButton(
//                                onClick = {
//                                    // Center map on user location
//                                },
//                                containerColor = AppColors.Primary,
//                                modifier = Modifier.size(48.dp),
//                                shape = CircleShape
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Outlined.MyLocation,
//                                    contentDescription = "My location",
//                                    tint = AppColors.OnPrimary,
//                                    modifier = Modifier.size(20.dp)
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//
//}
//
//@Composable
//private fun FilterChip(
//    filterType: FilterType,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .clickable { onClick() }
//            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(20.dp)),
//        shape = RoundedCornerShape(20.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = if (isSelected) filterType.color else AppColors.SurfaceVariant
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//    ) {
//        Row(
//            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(6.dp)
//        ) {
//            Icon(
//                imageVector = filterType.icon,
//                contentDescription = filterType.displayName,
//                modifier = Modifier.size(16.dp),
//                tint = if (isSelected) Color.White else AppColors.OnSurfaceVariant
//            )
//            Text(
//                text = filterType.displayName,
//                fontSize = 12.sp,
//                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
//                color = if (isSelected) Color.White else AppColors.OnSurfaceVariant,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}