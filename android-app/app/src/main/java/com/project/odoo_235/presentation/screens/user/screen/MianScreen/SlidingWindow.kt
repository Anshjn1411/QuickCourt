package com.project.odoo_235.presentation.screens.user.screen.MianScreen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.odoo_235.data.models.Court
import com.project.odoo_235.presentation.navigation.Screen
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtBottomBar
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtTopBar
import com.project.odoo_235.ui.theme.AppColors
import kotlin.math.round
import com.project.odoo_235.R
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.data.models.Location
import com.project.odoo_235.data.models.Ratings
import com.project.odoo_235.presentation.screens.AutheScreen.AuthViewModel
import com.project.odoo_235.presentation.screens.user.screen.components.QuickCourtSearchBar
import com.project.odoo_235.ui.theme.AppColors1
import com.project.odoo_235.ui.theme.Odoo_235Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSlider(
    modifier: Modifier = Modifier,
    height: Dp = 200.dp
) {
    val images = listOf(
        R.drawable.img,
        R.drawable.img_1,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })

    // Auto-slide effect every 2 sec
    LaunchedEffect(pagerState) {
        while (true) {
            delay(2000)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier, // ✅ now it's reusable, doesn’t force full screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Image $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Indicator (dots)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(images.size) { index ->
                val color =
                    if (pagerState.currentPage == index) AppColors1.SportsDark else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}


class FakeMainViewModel : ViewModel() {
    // Fake data for preview
    private val _currentCity = MutableStateFlow("Pune")
    val currentCity: StateFlow<String> = _currentCity
    

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchCurrentCity(context: Context) { /* no-op for preview */ }
    fun fetchCourts() { /* no-op for preview */ }
}
