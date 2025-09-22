// app/src/main/java/.../presentation/MianScreen/MainViewModel.kt
package com.project.odoo_235.presentation.screens.user.screen.MianScreen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.data.api.RetrofitInstance
import com.project.odoo_235.data.datastore.CachedUser
import com.project.odoo_235.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    private val sessionManager = UserSessionManager(application)
    private val api by lazy { RetrofitInstance.getApi(sessionManager) }

    // User location and name management
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName


    private val _locationLoading = MutableStateFlow(false)
    val locationLoading: StateFlow<Boolean> = _locationLoading

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError

    // Current city (optional UI)
    private val _currentCity = MutableStateFlow("")
    val currentCity: StateFlow<String> = _currentCity

    // Courts list with pagination
    private val _courts = MutableStateFlow<List<Court>>(emptyList())
    val courts: StateFlow<List<Court>> = _courts

    private val _pagination = MutableStateFlow<CourtsListPagination?>(null)
    val pagination: StateFlow<CourtsListPagination?> = _pagination

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Filters/sort state
    private val _sportType = MutableStateFlow<String?>(null)
    private val _cityFilter = MutableStateFlow<String?>(null)
    private val _stateFilter = MutableStateFlow<String?>(null)
    private val _minPrice = MutableStateFlow<String?>(null)
    private val _maxPrice = MutableStateFlow<String?>(null)
    private val _amenitiesCsv = MutableStateFlow<String?>(null)
    private val _ratingMin = MutableStateFlow<String?>(null)
    private val _sortBy = MutableStateFlow<String?>("createdAt")
    private val _sortOrder = MutableStateFlow<String?>("desc")
    private val _page = MutableStateFlow(1)
    private val _limit = MutableStateFlow(10)

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Nearby search
    private val _nearbyCourts = MutableStateFlow<List<Court>>(emptyList())
    val nearbyCourts: StateFlow<List<Court>> = _nearbyCourts

    private val _nearbyParams = MutableStateFlow<CourtsSearchParams?>(null)
    val nearbyParams: StateFlow<CourtsSearchParams?> = _nearbyParams

    // Review submission
    private val _reviewSubmitting = MutableStateFlow(false)
    val reviewSubmitting: StateFlow<Boolean> = _reviewSubmitting

    init {
        loadUserData()
        loadCurrentLocation()
    }

    private fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Get user name from session manager or SharedPreferences
                val cachedUserName = sessionManager.getUser()
                _userName.value = cachedUserName?.name!!
            } catch (e: Exception) {
                _userName.value = "User"
            }
        }
    }

    // Load current location city name
    fun loadCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _locationLoading.value = true
            _locationError.value = null

            try {
                // First try to get cached location
                val cachedCity = getCachedCity()
                if (cachedCity.isNotEmpty()) {
                    _currentCity.value = cachedCity
                    _locationLoading.value = false
                    return@launch
                }

                // If no cached location, get live location
                val location = getCurrentLatLng(context)
                if (location != null) {
                    val cityName = getCityNameFromCoordinates(location.first, location.second)
                    if (cityName.isNotEmpty()) {
                        _currentCity.value = cityName
                        cacheCity(cityName)
                    } else {
                        _currentCity.value = "Unknown Location"
                        _locationError.value = "Unable to determine city name"
                    }
                } else {
                    _currentCity.value = "Location Unavailable"
                    _locationError.value = "Unable to get current location"
                }
            } catch (e: Exception) {
                _currentCity.value = "Location Error"
                _locationError.value = e.localizedMessage
            } finally {
                _locationLoading.value = false
            }
        }
    }

    // Get city name from coordinates using Geocoder
    private suspend fun getCityNameFromCoordinates(latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    // Try to get city name in order of preference
                    address.locality
                        ?: address.subAdminArea
                        ?: address.subLocality
                        ?: "Unknown City"
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    // Cache city name in SharedPreferences
    private fun cacheCity(cityName: String) {
        try {
            val sharedPref = context.getSharedPreferences("user_location_cache", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("cached_city", cityName)
                putLong("cache_timestamp", System.currentTimeMillis())
                apply()
            }
        } catch (e: Exception) {
            // Handle caching error silently
        }
    }

    // Get cached city name (valid for 24 hours)
    private fun getCachedCity(): String {
        return try {
            val sharedPref = context.getSharedPreferences("user_location_cache", Context.MODE_PRIVATE)
            val cachedCity = sharedPref.getString("cached_city", "") ?: ""
            val cacheTimestamp = sharedPref.getLong("cache_timestamp", 0)
            val currentTime = System.currentTimeMillis()
            val cacheValidityPeriod = 24 * 60 * 60 * 1000L // 24 hours

            if (cachedCity.isNotEmpty() && (currentTime - cacheTimestamp) < cacheValidityPeriod) {
                cachedCity
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    // Force refresh location
    fun refreshLocation() {
        loadCurrentLocation()
    }



    fun setFilters(
        sportType: String? = _sportType.value,
        city: String? = _cityFilter.value,
        state: String? = _stateFilter.value,
        minPrice: String? = _minPrice.value,
        maxPrice: String? = _maxPrice.value,
        amenitiesCsv: String? = _amenitiesCsv.value,
        ratingMin: String? = _ratingMin.value,
        sortBy: String? = _sortBy.value,
        sortOrder: String? = _sortOrder.value,
        limit: Int? = _limit.value
    ) {
        _sportType.value = sportType
        _cityFilter.value = city
        _stateFilter.value = state
        _minPrice.value = minPrice
        _maxPrice.value = maxPrice
        _amenitiesCsv.value = amenitiesCsv
        _ratingMin.value = ratingMin
        _sortBy.value = sortBy
        _sortOrder.value = sortOrder
        if (limit != null) _limit.value = limit
        _page.value = 1
        fetchCourts()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        // Use search on server by city/sport if query matches; otherwise client filter
        // Basic approach: map to city or sport if matches known values, else filter client-side
    }

    fun fetchCourts(page: Int = _page.value) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _error.value = null
            try {
                val resp = api.getCourts(
                    sportType = _sportType.value,
                    city = _cityFilter.value,
                    state = _stateFilter.value,
                    minPrice = _minPrice.value,
                    maxPrice = _maxPrice.value,
                    amenitiesCsv = _amenitiesCsv.value,
                    rating = _ratingMin.value,
                    page = page,
                    limit = _limit.value,
                    sortBy = _sortBy.value,
                    sortOrder = _sortOrder.value
                )
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    _courts.value = body.courts
                    _pagination.value = body.pagination
                    _page.value = body.pagination.currentPage
                } else {
                    _error.value = resp.errorBody()?.string() ?: resp.message()
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun nextPage() {
        val p = _pagination.value ?: return
        if (p.hasNextPage) fetchCourts(_page.value + 1)
    }
    fun prevPage() {
        val p = _pagination.value ?: return
        if (p.hasPrevPage) fetchCourts(_page.value - 1)
    }

    @SuppressLint("MissingPermission")
    fun searchNearby(
        context: Context,
        radiusKm: Int = 10,
        sportType: String? = null,
        maxPrice: String? = null,
        staticLatLng: Pair<Double, Double>? = null // for testing
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _error.value = null
            try {
                val (lat, lng) = staticLatLng ?: getCurrentLatLng(context) ?: run {
                    _error.value = "Location unavailable"
                    _loading.value = false
                    return@launch
                }
                val resp = api.searchCourtsByLocation(lat = lat, lng = lng, radiusKm = radiusKm, sportType = sportType, maxPrice = maxPrice)
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    _nearbyCourts.value = body.courts
                    _nearbyParams.value = body.searchParams
                } else {
                    _error.value = resp.errorBody()?.string() ?: resp.message()
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLatLng(context: Context): Pair<Double, Double>? {
        return suspendCancellableCoroutine { cont ->
            val fused = LocationServices.getFusedLocationProviderClient(context)
            fused.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) cont.resume(Pair(loc.latitude, loc.longitude))
                    else {
                        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).setMaxUpdates(1).build()
                        fused.requestLocationUpdates(req, object : LocationCallback() {
                            override fun onLocationResult(result: LocationResult) {
                                fused.removeLocationUpdates(this)
                                val l = result.lastLocation
                                cont.resume(if (l != null) Pair(l.latitude, l.longitude) else null)
                            }
                        }, Looper.getMainLooper())
                    }
                }.addOnFailureListener { cont.resume(null) }
        }
    }

    fun submitCourtReview(courtId: String, rating: Int, comment: String?, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _reviewSubmitting.value = true
            try {
                val resp = api.addCourtReview(courtId, CourtReviewRequest(rating, comment))
                if (resp.isSuccessful && resp.body() != null && resp.body()!!.success) {
                    // Refresh court or list if needed
                    onDone(true, "Review submitted")
                } else {
                    onDone(false, resp.errorBody()?.string() ?: resp.message())
                }
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Failed to submit review")
            } finally {
                _reviewSubmitting.value = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentCity(context: Context) {
        _currentCity.value = _currentCity.value.ifBlank { "Gandhinagar" }
    }
}