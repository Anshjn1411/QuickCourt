package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.JsonObject
import com.project.odoo_235.data.models.Booking
import com.project.odoo_235.ui.theme.AppColors
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingSuccessScreen(
    bookingViewModel: BookingViewModel,
    onGoHome: () -> Unit,
    onViewReceipt: () -> Unit
) {
    val bookingState by bookingViewModel.bookingState.collectAsState()

    // Animations
    var showContent by remember { mutableStateOf(false) }
    var showSuccessIcon by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200); showSuccessIcon = true
        delay(500); showContent = true
        delay(300); showDetails = true
    }

    // Only proceed when we have a confirmed booking
    val booking = (bookingState as? BookingViewModel.UiBookingState.Success)?.booking
    if (booking == null) {
        EnhancedLoadingState()
        return
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val formattedDate = try {
        val local = booking.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        local.format(dateFormatter)
    } catch (e: Exception) {
        booking.date.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        EnhancedSuccessHeader(showSuccessIcon = showSuccessIcon, showContent = showContent)

        AnimatedVisibility(
            visible = showDetails,
            enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(600)) + fadeIn(animationSpec = tween(600))
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                EnhancedBookingDetailsCard(booking = booking, formattedDate = formattedDate)
                BookingStatusTimeline()
                QuickActionsSection(onGoHome = onGoHome, onViewReceipt = onViewReceipt)
                AdditionalInfoCard()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun EnhancedLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = AppColors.Primary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Processing your booking...",
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.OnSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Please wait a moment",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
fun EnhancedSuccessHeader(
    showSuccessIcon: Boolean,
    showContent: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.Success,
                            AppColors.Success.copy(alpha = 0.8f),
                            AppColors.Primary
                        )
                    )
                )
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Success Icon
                AnimatedVisibility(
                    visible = showSuccessIcon,
                    enter = scaleIn(
                        initialScale = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(500))
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                AppColors.OnPrimary.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .background(AppColors.OnPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                modifier = Modifier.size(60.dp),
                                tint = AppColors.Success
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Animated Content
                AnimatedVisibility(
                    visible = showContent,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(600)
                    ) + fadeIn(animationSpec = tween(600))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Booking Confirmed!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = AppColors.OnPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your court has been successfully booked",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = AppColors.OnPrimary.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.OnPrimary.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = null,
                                    tint = AppColors.OnPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Get ready to play!",
                                    color = AppColors.OnPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnhancedBookingDetailsCard(
    booking: Booking,
    formattedDate: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(AppColors.Primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Booking Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AppColors.OnSurface)
                    Text("Everything looks perfect!", style = MaterialTheme.typography.bodyMedium, color = AppColors.OnSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceVariant), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    EnhancedDetailRow(
                        icon = Icons.Default.Stadium,
                        label = "Court",
                        value = (booking.court as? JsonObject)?.get("name")?.asString
                            ?: (booking.court as? Map<*, *>)?.get("name")?.toString()
                            ?: "Court"
                        ,
                        iconColor = AppColors.Primary
                    )
                    EnhancedDetailRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Date",
                        value = formattedDate,
                        iconColor = AppColors.Info
                    )
                    EnhancedDetailRow(
                        icon = Icons.Default.AccessTime,
                        label = "Time",
                        value = "${booking.startTime} - ${booking.endTime}",
                        iconColor = AppColors.Warning
                    )
                    EnhancedDetailRow(
                        icon = Icons.Default.Schedule,
                        label = "Duration",
                        value = "${booking.duration} hr",
                        iconColor = AppColors.Success
                    )
                    EnhancedDetailRow(
                        icon = Icons.Default.Payments,
                        label = "Payment",
                        value = booking.paymentStatus.replaceFirstChar { it.uppercase() } +
                                (booking.paymentMethod?.let { " • ${it.uppercase()}" } ?: ""),
                        iconColor = AppColors.Primary,
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(colors = CardDefaults.cardColors(containerColor = AppColors.Primary.copy(alpha = 0.1f)), shape = RoundedCornerShape(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Amount Paid", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.OnSurface)
                        Text(
                            text = when (booking.paymentStatus) {
                                "paid" -> "Payment successful"
                                "pending" -> "Payment pending"
                                "failed" -> "Payment failed"
                                "refunded" -> "Payment refunded"
                                else -> "Payment: ${booking.paymentStatus}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (booking.paymentStatus == "paid") AppColors.Success else AppColors.OnSurfaceVariant
                        )
                    }
                    Text(
                        text = "₹${booking.totalAmount.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.Primary
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedDetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.OnSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.OnSurface
            )
        }
    }

    if (!isLast) {
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            color = AppColors.Outline.copy(alpha = 0.3f),
            modifier = Modifier.padding(start = 52.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun BookingStatusTimeline() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Booking Status",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            TimelineItem(
                icon = Icons.Default.CheckCircle,
                title = "Booking Confirmed",
                subtitle = "Your booking has been confirmed",
                isCompleted = true,
                isLast = false
            )

            TimelineItem(
                icon = Icons.Default.Email,
                title = "Confirmation Email Sent",
                subtitle = "Check your email for details",
                isCompleted = true,
                isLast = false
            )

            TimelineItem(
                icon = Icons.Default.SportsTennis,
                title = "Ready to Play",
                subtitle = "Arrive 15 minutes before your slot",
                isCompleted = false,
                isLast = true
            )
        }
    }
}

@Composable
fun TimelineItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isLast: Boolean
) {
    Row {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isCompleted) AppColors.Success else AppColors.OnSurfaceVariant.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isCompleted) AppColors.OnPrimary else AppColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            if (isCompleted) AppColors.Success.copy(alpha = 0.3f) else AppColors.OnSurfaceVariant.copy(alpha = 0.2f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isCompleted) AppColors.OnSurface else AppColors.OnSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.OnSurfaceVariant
            )

            if (!isLast) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onGoHome: () -> Unit,
    onViewReceipt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.OnSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewReceipt,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.Primary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(AppColors.Primary, AppColors.PrimaryVariant)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = "Receipt",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Receipt",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onGoHome,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Go Home",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun AdditionalInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.Info.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AppColors.Info,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Important Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.OnSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "• Please arrive 15 minutes before your scheduled time\n" +
                        "• Bring valid ID for verification\n" +
                        "• Cancellation allowed up to 2 hours before booking\n" +
                        "• Contact support for any changes or queries",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.OnSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}