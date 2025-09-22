import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.odoo_235.data.datastore.CachedUser
import com.project.odoo_235.data.datastore.UserSessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import com.project.odoo_235.presentation.screens.user.screen.MianScreen.MainViewModel
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtBottomBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtTopBar
import com.project.odoo_235.ui.theme.AppColors

@Composable
fun ProfileScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    userSessionManager: UserSessionManager,
    onLogoutSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val userState = produceState<CachedUser?>(initialValue = null, userSessionManager) {
        userSessionManager.userData.collectLatest { value = it }
    }

    val user = userState.value

    val userName by mainViewModel.userName.collectAsState()
    val currentCity by mainViewModel.currentCity.collectAsState()
    val locationLoading by mainViewModel.locationLoading.collectAsState()

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
                currentRoute = "Profile"
            )
        },
        containerColor = AppColors.Background
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = Color(0xFFF5F5F5)
        ) {
            if (user == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // User Header
                    item {
                        UserHeaderCard(user = user)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Menu Items
                    item {
                        MenuItemCard(
                            icon = Icons.Default.Person,
                            title = "Account",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Account click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.ShoppingBag,
                            title = "Your Booking",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Your Booking click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.AttachMoney,
                            title = "Refunds",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Refunds click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.Favorite,
                            title = "Favourite Venues",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Favourite Venues click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.Support,
                            title = "Support",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Support click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.PrivacyTip,
                            title = "Privacy Policy",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Privacy Policy click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.Description,
                            title = "Terms of use",
                            iconColor = Color(0xFF4CAF50),
                            onClick = { /* Handle Terms of use click */ }
                        )
                    }

                    item {
                        MenuItemCard(
                            icon = Icons.Default.ExitToApp,
                            title = "Logout",
                            iconColor = Color(0xFFE53935),
                            showChevron = false,
                            onClick = {
                                coroutineScope.launch {
                                    userSessionManager.clearUser()
                                    onLogoutSuccess()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserHeaderCard(user: CachedUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString(""),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color.Black
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconColor: Color,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = if (title == "Logout") Color(0xFFE53935) else Color.Black,
                modifier = Modifier.weight(1f)
            )

            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}