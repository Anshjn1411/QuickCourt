// presentation/screens/bookingScreen/BookingViewModel.kt
package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.project.odoo_235.data.api.RetrofitInstance
import com.project.odoo_235.data.datastore.UserSessionManager
import com.project.odoo_235.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = UserSessionManager(application)
    private val api by lazy { RetrofitInstance.getApi(sessionManager) }

    // Existing state
    private val _venueDetail = MutableStateFlow<Court?>(null)
    val venueDetail: StateFlow<Court?> = _venueDetail

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    sealed class UiBookingState {
        data object Initial : UiBookingState()
        data object Processing : UiBookingState()
        data class ReadyForBooking(
            val courtId: String,
            val dateIso: String,
            val startTime: String,
            val endTime: String,
            val playersCount: Int
        ) : UiBookingState()
        data class Success(val booking: Booking) : UiBookingState()
        data class Failed(val reason: String) : UiBookingState()
    }

    private val _bookingState = MutableStateFlow<UiBookingState>(UiBookingState.Initial)
    val bookingState: StateFlow<UiBookingState> = _bookingState

    // List + pagination
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _pagination = MutableStateFlow<BookingsListPagination?>(null)
    val pagination: StateFlow<BookingsListPagination?> = _pagination

    private val _listLoading = MutableStateFlow(false)
    val listLoading: StateFlow<Boolean> = _listLoading

    private val _listError = MutableStateFlow<String?>(null)
    val listError: StateFlow<String?> = _listError

    private val _page = MutableStateFlow(1)
    private val _limit = MutableStateFlow(10)
    private val _sortBy = MutableStateFlow("createdAt")
    private val _sortOrder = MutableStateFlow("desc")
    private val _statusFilter = MutableStateFlow<String?>(null)

    // Details
    private val _bookingDetail = MutableStateFlow<Booking?>(null)
    val bookingDetail: StateFlow<Booking?> = _bookingDetail

    // Action states
    private val _cancelSubmitting = MutableStateFlow(false)
    val cancelSubmitting: StateFlow<Boolean> = _cancelSubmitting

    private val _reviewSubmitting = MutableStateFlow(false)
    val reviewSubmitting: StateFlow<Boolean> = _reviewSubmitting

    private val _paymentUpdating = MutableStateFlow(false)
    val paymentUpdating: StateFlow<Boolean> = _paymentUpdating

    // Analytics
    private val _analytics = MutableStateFlow<JsonObject?>(null)
    val analytics: StateFlow<JsonObject?> = _analytics

    // -------- Venue detail (existing) ----------
    fun fetchVenueDetail(venueId: String) {
        viewModelScope.launch {
            resetBookingState()
            _loading.value = true
            _error.value = null
            try {
                val response = api.getCourtById(venueId)
                if (response.isSuccessful && response.body() != null) {
                    _venueDetail.value = response.body()!!.court
                } else {
                    _error.value = response.errorBody()?.string() ?: "Failed to load venue details"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    // in BookingViewModel.kt


    private val _availability = MutableStateFlow<List<TimeSlot>>(emptyList())
    val availability: StateFlow<List<TimeSlot>> = _availability

    // Pending booking selection (for payment mode screen)
    data class PendingBooking(
        val venueId: String,
        val dateIso: String,
        val slotRange: String
    )
    private val _pendingBooking = MutableStateFlow<PendingBooking?>(null)
    val pendingBooking: StateFlow<PendingBooking?> = _pendingBooking
    fun setPendingBooking(venueId: String, dateIso: String, slotRange: String) {
        _pendingBooking.value = PendingBooking(venueId, dateIso, slotRange)
    }

    fun fetchAvailabilityForDate(courtId: String, dateIso: String) {
        viewModelScope.launch {
            try {
                val resp = api.getCourtAvailability(courtId = courtId, dateIso = dateIso)
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    val slots = body.timeSlots?.map {
                        TimeSlot(startTime = it.startTime, endTime = it.endTime, available = it.available == true)
                    } ?: emptyList()
                    _availability.value = slots
                } else {
                    _availability.value = emptyList()
                }
            } catch (_: Exception) {
                _availability.value = emptyList()
            }
        }
    }

    // -------- Booking creation (existing) ----------
    fun prepareBooking(venueId: String, selectedDate: LocalDate, selectedSlot: String, playersCount: Int = 1) {
        viewModelScope.launch {
            _bookingState.value = UiBookingState.Processing
            _error.value = null
            try {
                val parts = selectedSlot.split("-")
                if (parts.size != 2) {
                    _bookingState.value = UiBookingState.Failed("Invalid time slot format")
                    return@launch
                }
                val dateIso = selectedDate.toString()
                val isAvailable = checkSlotAvailability(venueId, dateIso, parts[0], parts[1])
                if (!isAvailable) {
                    _bookingState.value = UiBookingState.Failed("Selected slot is not available")
                    return@launch
                }
                _bookingState.value = UiBookingState.ReadyForBooking(
                    courtId = venueId, dateIso = dateIso, startTime = parts[0], endTime = parts[1], playersCount = playersCount
                )
            } catch (e: Exception) {
                _bookingState.value = UiBookingState.Failed(e.message ?: "Failed to prepare booking")
            }
        }
    }

    fun bookVenueDirect(
        venueId: String,
        selectedDate: LocalDate,
        selectedSlot: String,
        playersCount: Int = 1,
        paymentMethod: String? = null,
        userNotes: String? = null
    ) {
        viewModelScope.launch {
            _bookingState.value = UiBookingState.Processing
            _error.value = null
            try {
                val parts = selectedSlot.split("-")
                if (parts.size != 2) {
                    _bookingState.value = UiBookingState.Failed("Invalid time slot format")
                    return@launch
                }
                val dateIso = selectedDate.toString()

                val available = checkSlotAvailability(venueId, dateIso, parts[0], parts[1])
                if (!available) {
                    _bookingState.value = UiBookingState.Failed("Selected slot is not available")
                    return@launch
                }

                val request = CreateBookingRequest(
                    courtId = venueId,
                    date = dateIso,
                    startTime = parts[0],
                    endTime = parts[1],
                    players = Players(count = playersCount, names = emptyList()),
                    additionalServices = AdditionalServices(equipment = false, lighting = false, coaching = false, cleaning = false),
                    userNotes = userNotes,
                    paymentMethod = paymentMethod
                )

                val response = api.createBooking(request)
                if (response.isSuccessful && response.body() != null) {
                    val body: BookingSuccessResponse = response.body()!!
                    _bookingState.value = UiBookingState.Success(body.booking)
                } else {
                    _bookingState.value = UiBookingState.Failed(response.errorBody()?.string() ?: response.message())
                }
            } catch (e: Exception) {
                _bookingState.value = UiBookingState.Failed(e.message ?: "Booking failed")
            }
        }
    }

    private suspend fun checkSlotAvailability(courtId: String, dateIso: String, startTime: String, endTime: String): Boolean {
        return try {
            val resp = api.getCourtAvailability(courtId = courtId, dateIso = dateIso)
            if (resp.isSuccessful && resp.body() != null) {
                val body = resp.body()!!
                val slot = body.timeSlots?.firstOrNull { it.startTime == startTime && it.endTime == endTime }
                slot?.available == true
            } else false
        } catch (_: Exception) { false }
    }

    // -------- User bookings list ----------
    fun setListControls(page: Int? = null, limit: Int? = null, sortBy: String? = null, sortOrder: String? = null, status: String? = _statusFilter.value) {
        page?.let { _page.value = it }
        limit?.let { _limit.value = it }
        sortBy?.let { _sortBy.value = it }
        sortOrder?.let { _sortOrder.value = it }
        _statusFilter.value = status
        fetchUserBookings(_page.value)
    }

    fun fetchUserBookings(page: Int = _page.value) {
        viewModelScope.launch {
            _listLoading.value = true
            _listError.value = null
            try {
                val resp = api.getUserBookings(
                    page = page,
                    limit = _limit.value,
                    sortBy = _sortBy.value,
                    sortOrder = _sortOrder.value,
                    status = _statusFilter.value
                )
                if (resp.isSuccessful && resp.body() != null) {
                    val body = resp.body()!!
                    _bookings.value = body.bookings
                    _pagination.value = body.pagination
                    _page.value = body.pagination.currentPage
                } else {
                    _listError.value = resp.errorBody()?.string() ?: resp.message()
                }
            } catch (e: Exception) {
                _listError.value = e.localizedMessage
            } finally {
                _listLoading.value = false
            }
        }
    }
    fun nextPage() { _pagination.value?.let { if (it.hasNextPage) fetchUserBookings(_page.value + 1) } }
    fun prevPage() { _pagination.value?.let { if (it.hasPrevPage) fetchUserBookings(_page.value - 1) } }

    // -------- Details ----------
    fun fetchBookingDetails(id: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = api.getBookingById(id)
                if (resp.isSuccessful && resp.body() != null) {
                    _bookingDetail.value = resp.body()!!.booking
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

    // -------- Actions ----------
    fun cancelBooking(id: String, reason: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _cancelSubmitting.value = true
            try {
                val resp = api.cancelBooking(id, CancelBookingRequest(cancellationReason = reason))
                if (resp.isSuccessful && (resp.body()?.success == true)) {
                    fetchBookingDetails(id); fetchUserBookings(_page.value)
                    onDone(true, resp.body()?.message ?: "Booking cancelled")
                } else {
                    onDone(false, resp.errorBody()?.string() ?: resp.message())
                }
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Cancel failed")
            } finally {
                _cancelSubmitting.value = false
            }
        }
    }

    fun addBookingReview(id: String, rating: Int, review: String?, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _reviewSubmitting.value = true
            try {
                val resp = api.addBookingReview(id, BookingReviewRequest(rating = rating, review = review))
                if (resp.isSuccessful && (resp.body()?.success == true)) {
                    fetchBookingDetails(id); fetchUserBookings(_page.value)
                    onDone(true, resp.body()?.message ?: "Review submitted")
                } else {
                    onDone(false, resp.errorBody()?.string() ?: resp.message())
                }
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Review failed")
            } finally {
                _reviewSubmitting.value = false
            }
        }
    }

    fun updatePayment(id: String, paymentStatus: String, paymentMethod: String?, transactionId: String?, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _paymentUpdating.value = true
            try {
                val resp = api.updateBookingPayment(
                    id,
                    UpdatePaymentStatusRequest(paymentStatus = paymentStatus, transactionId = transactionId, paymentMethod = paymentMethod)
                )
                if (resp.isSuccessful && (resp.body()?.success == true)) {
                    fetchBookingDetails(id); fetchUserBookings(_page.value)
                    onDone(true, resp.body()?.message ?: "Payment updated")
                } else {
                    onDone(false, resp.errorBody()?.string() ?: resp.message())
                }
            } catch (e: Exception) {
                onDone(false, e.localizedMessage ?: "Payment update failed")
            } finally {
                _paymentUpdating.value = false
            }
        }
    }

    // -------- Analytics ----------
    fun fetchUserAnalytics() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val resp = api.getUserBookingAnalytics()
                if (resp.isSuccessful && resp.body() != null) {
                    _analytics.value = resp.body()
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

    fun resetBookingState() {
        _bookingState.value = UiBookingState.Initial
        _error.value = null
    }
}