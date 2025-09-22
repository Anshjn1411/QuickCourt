package com.project.odoo_235.presentation.screens.owner.mainscreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.odoo_235.data.api.RetrofitInstance
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FacilityDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val session = UserSessionManager(application)
    private val api by lazy { RetrofitInstance.getApi(session) }

    private val _allCourts = MutableStateFlow<List<Court>>(emptyList())
    private val _allBookings = MutableStateFlow<List<Booking>>(emptyList())
    
    // Filter states
    private val _selectedSportFilter = MutableStateFlow<String?>(null)
    private val _selectedStatusFilter = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")

    // Filtered data
    val courts: StateFlow<List<Court>> = combine(
        _allCourts,
        _selectedSportFilter,
        _selectedStatusFilter,
        _searchQuery
    ) { allCourts, sportFilter, statusFilter, searchQuery ->
        allCourts.filter { court ->
            val matchesSport = sportFilter == null || court.sportType.equals(sportFilter, ignoreCase = true)
            val matchesStatus = statusFilter == null || court.status.equals(statusFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() || 
                court.name.contains(searchQuery, ignoreCase = true) ||
                court.location.city.contains(searchQuery, ignoreCase = true) ||
                court.sportType.contains(searchQuery, ignoreCase = true)
            
            matchesSport && matchesStatus && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val bookings: StateFlow<List<Booking>> = _allBookings

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _ownerStats = MutableStateFlow<OwnerCourtStatistics?>(null)
    val ownerStats: StateFlow<OwnerCourtStatistics?> = _ownerStats

    private val _ownerAnalytics = MutableStateFlow<OwnerAnalytics?>(null)
    val ownerAnalytics: StateFlow<OwnerAnalytics?> = _ownerAnalytics

    // Filter options
    val availableSports: StateFlow<List<String>> = _allCourts.map { courts ->
        courts.map { it.sportType }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val availableStatuses: StateFlow<List<String>> = _allCourts.map { courts ->
        courts.map { it.status }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Derived counters (from stats or fallbacks)
    val totalCourts: StateFlow<Int> = ownerStats.map { it?.totalCourts ?: _allCourts.value.size }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val totalBookings: StateFlow<Int> = ownerStats.map { it?.totalBookings ?: _allBookings.value.size }.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val totalEarnings: StateFlow<Double> = ownerStats.map { it?.totalRevenue ?: 0.0 }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun refreshAll(periodDays: Int = 30) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _error.value = null
            try {
                // Owner courts
                val courtsResp = api.getOwnerCourts(page = 1, limit = 50)
                if (courtsResp.isSuccessful && courtsResp.body() != null) {
                    _allCourts.value = courtsResp.body()!!.courts
                } else {
                    _error.value = courtsResp.errorBody()?.string() ?: courtsResp.message()
                }

                // Owner bookings
                val bookingsResp = api.getOwnerBookings(page = 1, limit = 50)
                if (bookingsResp.isSuccessful && bookingsResp.body() != null) {
                    _allBookings.value = bookingsResp.body()!!.bookings
                } else {
                    _error.value = bookingsResp.errorBody()?.string() ?: bookingsResp.message()
                }

                // Statistics
                val statsResp = api.getOwnerCourtStatistics(periodDays = periodDays)
                if (statsResp.isSuccessful && statsResp.body() != null) {
                    _ownerStats.value = statsResp.body()!!.statistics
                } else {
                    _error.value = statsResp.errorBody()?.string() ?: statsResp.message()
                }

                // Analytics
                val analyticsResp = api.getOwnerCourtAnalytics(periodDays = periodDays)
                if (analyticsResp.isSuccessful && analyticsResp.body() != null) {
                    _ownerAnalytics.value = analyticsResp.body()!!.analytics
                } else {
                    _error.value = analyticsResp.errorBody()?.string() ?: analyticsResp.message()
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    // Filter functions
    fun setSportFilter(sport: String?) {
        _selectedSportFilter.value = sport
    }

    fun setStatusFilter(status: String?) {
        _selectedStatusFilter.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearAllFilters() {
        _selectedSportFilter.value = null
        _selectedStatusFilter.value = null
        _searchQuery.value = ""
    }
}