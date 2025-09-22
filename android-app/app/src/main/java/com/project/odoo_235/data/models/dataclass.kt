package com.project.odoo_235.data.models


import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

// ===========================
// Base/Generic API wrappers
// ===========================

data class ApiMessageResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)

data class ApiErrorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String,
    @SerializedName("requiresVerification") val requiresVerification: Boolean? = null,
    @SerializedName("email") val email: String? = null
)

// Some endpoints return a named payload key instead of "data". Use specific response classes below.

// ===========================
// Common/Shared Models
// ===========================

data class Avatar(
    @SerializedName("public_id") val publicId: String?,
    @SerializedName("url") val url: String?
)

data class UserRef(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
)

data class User(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String, // "User" | "Owner" | "Admin"
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("avatar") val avatar: Avatar? = null,
    @SerializedName("isVerified") val isVerified: Boolean? = null,
    @SerializedName("verificationReason") val verificationReason: String? = null,
    @SerializedName("createdAt") val createdAt: Date? = null,
    @SerializedName("updatedAt") val updatedAt: Date? = null
)

data class Coordinates(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class Location(
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("country") val country: String,
    @SerializedName("coordinates") val coordinates: Coordinates
)

data class Pricing(
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("peakHourPrice") val peakHourPrice: Double? = null,
    @SerializedName("weekendPrice") val weekendPrice: Double? = null,
    @SerializedName("currency") val currency: String,
    @SerializedName("hourlyRate") val hourlyRate: Boolean
)

data class Image(
    @SerializedName("public_id") val publicId: String,
    @SerializedName("url") val url: String
)

data class Capacity(
    @SerializedName("minPlayers") val minPlayers: Int,
    @SerializedName("maxPlayers") val maxPlayers: Int
)

data class Dimensions(
    @SerializedName("length") val length: Double,
    @SerializedName("width") val width: Double,
    @SerializedName("unit") val unit: String
)

data class Lighting(
    @SerializedName("available") val available: Boolean,
    @SerializedName("type") val type: String? = null,
    @SerializedName("additionalCost") val additionalCost: Double? = null
)

data class Equipment(
    @SerializedName("provided") val provided: Boolean,
    @SerializedName("items") val items: List<String>? = null,
    @SerializedName("rentalCost") val rentalCost: Double? = null
)

data class Availability(
    @SerializedName("isAvailable") val isAvailable: Boolean,
    @SerializedName("maintenanceMode") val maintenanceMode: Boolean,
    @SerializedName("maintenanceReason") val maintenanceReason: String? = null,
    @SerializedName("maintenanceEndDate") val maintenanceEndDate: Date? = null
)

data class DaySchedule(
    @SerializedName("open") val open: String,
    @SerializedName("close") val close: String,
    @SerializedName("closed") val closed: Boolean
)

data class OperatingHours(
    @SerializedName("monday") val monday: DaySchedule,
    @SerializedName("tuesday") val tuesday: DaySchedule,
    @SerializedName("wednesday") val wednesday: DaySchedule,
    @SerializedName("thursday") val thursday: DaySchedule,
    @SerializedName("friday") val friday: DaySchedule,
    @SerializedName("saturday") val saturday: DaySchedule,
    @SerializedName("sunday") val sunday: DaySchedule
)

data class Review(
    @SerializedName("user") val user: String, // user id
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String,
    @SerializedName("date") val date: Date
)

data class Ratings(
    @SerializedName("average") val average: Double,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("reviews") val reviews: List<Review>
)

data class CourtStats(
    @SerializedName("totalBookings") val totalBookings: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("averageBookingDuration") val averageBookingDuration: Double,
    @SerializedName("peakHours") val peakHours: List<String>,
    @SerializedName("popularDays") val popularDays: List<String>
)

data class Court(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String,
    @SerializedName("surfaceType") val surfaceType: String,
    @SerializedName("description") val description: String,
    @SerializedName("location") val location: Location,
    @SerializedName("pricing") val pricing: Pricing,
    @SerializedName("amenities") val amenities: List<String>,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("capacity") val capacity: Capacity,
    @SerializedName("dimensions") val dimensions: Dimensions,
    @SerializedName("lighting") val lighting: Lighting,
    @SerializedName("equipment") val equipment: Equipment,
    @SerializedName("availability") val availability: Availability,
    @SerializedName("operatingHours") val operatingHours: OperatingHours,
    @SerializedName("rules") val rules: List<String>,
    @SerializedName("owner") val owner: Any?, // Often populated with name/email, use UserRef in callers if needed
    @SerializedName("status") val status: String, // 'active' | 'inactive' | 'pending' | 'suspended'
    @SerializedName("approvalStatus") val approvalStatus: String, // 'pending'|'approved'|'rejected'
    @SerializedName("approvalDate") val approvalDate: Date? = null,
    @SerializedName("approvedBy") val approvedBy: Any? = null, // often populated with name
    @SerializedName("rejectionReason") val rejectionReason: String? = null,
    @SerializedName("ratings") val ratings: Ratings,
    @SerializedName("statistics") val statistics: CourtStats,
    @SerializedName("createdAt") val createdAt: Date? = null,
    @SerializedName("updatedAt") val updatedAt: Date? = null
)

data class AdditionalServices(
    @SerializedName("equipment") val equipment: Boolean,
    @SerializedName("lighting") val lighting: Boolean,
    @SerializedName("coaching") val coaching: Boolean,
    @SerializedName("cleaning") val cleaning: Boolean
)

data class AdditionalCosts(
    @SerializedName("equipment") val equipment: Double,
    @SerializedName("lighting") val lighting: Double,
    @SerializedName("coaching") val coaching: Double,
    @SerializedName("cleaning") val cleaning: Double
)

data class Players(
    @SerializedName("count") val count: Int,
    @SerializedName("names") val names: List<String>? = null
)

data class Booking(
    @SerializedName("_id") val id: String,
    @SerializedName("user") val user: Any?, // can be String id or populated User
    @SerializedName("court") val court: Any?, // can be String id or populated Court
    @SerializedName("owner") val owner: Any?,
    @SerializedName("date") val date: Date,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("duration") val duration: Double,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("paymentStatus") val paymentStatus: String,
    @SerializedName("paymentMethod") val paymentMethod: String? = null,
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("paymentDate") val paymentDate: Date? = null,
    @SerializedName("status") val status: String,
    @SerializedName("cancellationReason") val cancellationReason: String? = null,
    @SerializedName("cancelledBy") val cancelledBy: String? = null,
    @SerializedName("cancellationDate") val cancellationDate: Date? = null,
    @SerializedName("additionalServices") val additionalServices: AdditionalServices? = null,
    @SerializedName("additionalCosts") val additionalCosts: AdditionalCosts? = null,
    @SerializedName("players") val players: Players,
    @SerializedName("userNotes") val userNotes: String? = null,
    @SerializedName("ownerNotes") val ownerNotes: String? = null,
    @SerializedName("rating") val rating: Int? = null,
    @SerializedName("review") val review: String? = null,
    @SerializedName("reviewDate") val reviewDate: Date? = null,
    @SerializedName("createdAt") val createdAt: Date? = null,
    @SerializedName("updatedAt") val updatedAt: Date? = null
)

// ===========================
// Auth: Requests & Responses
// ===========================

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("role") val role: String // "User" | "Owner"
)
// Register uses multipart with 'avatar' file optionally

data class AuthSuccessResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class ForgotPasswordSuccessResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("resetToken") val resetToken: String? = null // present only in dev mode
)

data class ResetPasswordRequest(
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)
// Response is AuthSuccessResponse

data class VerifyEmailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: VerifyUserSnippet
)

data class VerifyUserSnippet(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("isVerified") val isVerified: Boolean
)

data class ResendVerificationRequest(
    @SerializedName("email") val email: String
)

data class CurrentUserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("user") val user: User
)

data class UpdatePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)
// Response is AuthSuccessResponse

// PUT /me/update uses multipart with optional 'avatar' file.
// Body fields that may be sent as text parts:
data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phonephone") val phonephone: String? = null // note backend uses "phonephone"
)

// ===========================
// Admin (via userController /admin)
// ===========================

data class AdminUsersListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("users") val users: List<User>
)

data class AdminSingleUserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("user") val user: User
)

data class AdminUpdateUserRoleRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null
)

// ===========================
// Courts: Requests & Responses
// ===========================

// POST /courts (multipart); Form fields are flattened; images[] as files.
// For Retrofit, send as Multipart parts. Shown here for reference:
data class CreateCourtForm(
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String,
    @SerializedName("surfaceType") val surfaceType: String,
    @SerializedName("description") val description: String,
    @SerializedName("address") val address: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("country") val country: String? = "India",
    @SerializedName("latitude") val latitude: String, // backend parsesFloat
    @SerializedName("longitude") val longitude: String,
    @SerializedName("basePrice") val basePrice: String,
    @SerializedName("peakHourPrice") val peakHourPrice: String? = null,
    @SerializedName("weekendPrice") val weekendPrice: String? = null,
    @SerializedName("currency") val currency: String? = "INR",
    @SerializedName("hourlyRate") val hourlyRate: String? = "true",
    @SerializedName("amenities") val amenities: List<String>? = emptyList(),
    @SerializedName("minPlayers") val minPlayers: String,
    @SerializedName("maxPlayers") val maxPlayers: String,
    @SerializedName("length") val length: String,
    @SerializedName("width") val width: String,
    @SerializedName("unit") val unit: String? = "meters",
    @SerializedName("lightingAvailable") val lightingAvailable: String? = "false",
    @SerializedName("lightingType") val lightingType: String? = null,
    @SerializedName("lightingAdditionalCost") val lightingAdditionalCost: String? = null,
    @SerializedName("equipmentProvided") val equipmentProvided: String? = "false",
    @SerializedName("equipmentItems") val equipmentItemsJson: String? = null, // JSON array string
    @SerializedName("equipmentRentalCost") val equipmentRentalCost: String? = null,
    @SerializedName("isAvailable") val isAvailable: String? = "true",
    @SerializedName("maintenanceMode") val maintenanceMode: String? = "false",
    @SerializedName("maintenanceReason") val maintenanceReason: String? = null,
    @SerializedName("maintenanceEndDate") val maintenanceEndDate: String? = null,
    @SerializedName("operatingHours") val operatingHoursJson: String? = null, // JSON object string
    @SerializedName("rules") val rules: List<String>? = emptyList()
)

data class CourtResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("court") val court: Court
)

data class CourtsListPagination(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalCourts") val totalCourts: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)

data class CourtsListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("courts") val courts: List<Court>,
    @SerializedName("pagination") val pagination: CourtsListPagination
)

data class CourtsSearchParams(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("radius") val radius: Int,
    @SerializedName("sportType") val sportType: String? = null,
    @SerializedName("maxPrice") val maxPrice: String? = null
)

data class CourtsSearchByLocationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("courts") val courts: List<Court>,
    @SerializedName("searchParams") val searchParams: CourtsSearchParams
)

data class OwnerCourtsListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("courts") val courts: List<Court>,
    @SerializedName("pagination") val pagination: CourtsListPagination
)

data class ToggleAvailabilityRequest(
    @SerializedName("isAvailable") val isAvailable: Boolean,
    @SerializedName("maintenanceMode") val maintenanceMode: Boolean,
    @SerializedName("maintenanceReason") val maintenanceReason: String? = null,
    @SerializedName("maintenanceEndDate") val maintenanceEndDate: String? = null // ISO date string
)

data class CourtReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String? = null
)

data class OwnerStatsTopCourt(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("bookings") val bookings: Int,
    @SerializedName("rating") val rating: Double
)

data class OwnerCourtStatistics(
    @SerializedName("totalCourts") val totalCourts: Int,
    @SerializedName("activeCourts") val activeCourts: Int,
    @SerializedName("pendingApproval") val pendingApproval: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("totalBookings") val totalBookings: Int,
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("recentCourts") val recentCourts: Int,
    @SerializedName("sportTypeStats") val sportTypeStats: Map<String, Int>,
    @SerializedName("statusStats") val statusStats: Map<String, Int>,
    @SerializedName("topCourts") val topCourts: List<OwnerStatsTopCourt>
)

data class OwnerStatisticsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("statistics") val statistics: OwnerCourtStatistics
)

data class CourtPerformance(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("bookings") val bookings: Int,
    @SerializedName("rating") val rating: Double,
    @SerializedName("status") val status: String,
    @SerializedName("approvalStatus") val approvalStatus: String
)

data class OwnerAnalytics(
    @SerializedName("totalCourts") val totalCourts: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("totalBookings") val totalBookings: Int,
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("revenueBySport") val revenueBySport: Map<String, Double>,
    @SerializedName("bookingsBySport") val bookingsBySport: Map<String, Int>,
    @SerializedName("courtPerformance") val courtPerformance: List<CourtPerformance>
)

data class OwnerAnalyticsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("analytics") val analytics: OwnerAnalytics
)

data class AdminCourtsApprovalPagination(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalCourts") val totalCourts: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)

data class AdminCourtsApprovalListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("courts") val courts: List<Court>,
    @SerializedName("pagination") val pagination: AdminCourtsApprovalPagination
)

data class ApproveRejectCourtRequest(
    @SerializedName("approvalStatus") val approvalStatus: String, // "approved" | "rejected" | "pending"
    @SerializedName("rejectionReason") val rejectionReason: String? = null
)

// ===========================
// Bookings: Requests & Responses
// ===========================

data class CreateBookingRequest(
    @SerializedName("courtId") val courtId: String,
    @SerializedName("date") val date: String, // ISO date string
    @SerializedName("startTime") val startTime: String, // "HH:MM"
    @SerializedName("endTime") val endTime: String, // "HH:MM"
    @SerializedName("players") val players: Players,
    @SerializedName("additionalServices") val additionalServices: AdditionalServices? = null,
    @SerializedName("userNotes") val userNotes: String? = null,
    @SerializedName("paymentMethod") val paymentMethod: String? = null // 'online'|'cash'|'card'|'upi'
)

data class BookingSuccessResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("booking") val booking: Booking,
    @SerializedName("message") val message: String? = null
)

data class BookingsListPagination(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalBookings") val totalBookings: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)

data class BookingsListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("bookings") val bookings: List<Booking>,
    @SerializedName("pagination") val pagination: BookingsListPagination
)

data class UpdateBookingStatusRequest(
    @SerializedName("status") val status: String, // 'pending'|'confirmed'|'cancelled'|'completed'|'no-show'
    @SerializedName("cancellationReason") val cancellationReason: String? = null
)

data class UpdatePaymentStatusRequest(
    @SerializedName("paymentStatus") val paymentStatus: String, // 'pending'|'paid'|'failed'|'refunded'|'cancelled'
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("paymentMethod") val paymentMethod: String? = null
)

data class BookingReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("review") val review: String? = null
)

data class CancelBookingRequest(
    @SerializedName("cancellationReason") val cancellationReason: String? = null
)

data class TimeSlot(
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("available") val available: Boolean
)

data class ExistingBookingSlot(
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String
)

data class AvailabilityCourtSnippet(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String
)

data class OperatingHoursSnippet(
    @SerializedName("open") val open: String,
    @SerializedName("close") val close: String
)

data class CourtAvailabilityResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("available") val available: Boolean? = null, // present only when closed
    @SerializedName("message") val message: String? = null,
    @SerializedName("court") val court: AvailabilityCourtSnippet? = null,
    @SerializedName("date") val date: Date? = null,
    @SerializedName("operatingHours") val operatingHours: OperatingHoursSnippet? = null,
    @SerializedName("timeSlots") val timeSlots: List<TimeSlot>? = null,
    @SerializedName("existingBookings") val existingBookings: List<ExistingBookingSlot>? = null
)

// ===========================
// Admin Controller: Requests & Responses
// ===========================

data class AdminStats(
    @SerializedName("totalUsers") val totalUsers: Int,
    @SerializedName("totalCourts") val totalCourts: Int,
    @SerializedName("totalBookings") val totalBookings: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("pendingApprovals") val pendingApprovals: Int,
    @SerializedName("activeCourts") val activeCourts: Int,
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("userGrowth") val userGrowth: Int,
    @SerializedName("courtGrowth") val courtGrowth: Int,
    @SerializedName("revenueGrowth") val revenueGrowth: Int,
    @SerializedName("period") val period: Int
)

data class AdminStatsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("stats") val stats: AdminStats
)

data class SeriesPoint(
    @SerializedName("_id") val date: String, // 'YYYY-MM-DD'
    @SerializedName("count") val count: Int
)

data class BookingSeriesPoint(
    @SerializedName("_id") val date: String,
    @SerializedName("count") val count: Int,
    @SerializedName("revenue") val revenue: Double
)

data class DistPoint(
    @SerializedName("_id") val key: String,
    @SerializedName("count") val count: Int
)

data class TopCourtAgg(
    @SerializedName("name") val name: String,
    @SerializedName("sportType") val sportType: String,
    @SerializedName("totalBookings") val totalBookings: Int,
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("ratings") val ratings: Ratings? = null
)

data class PlatformAnalytics(
    @SerializedName("userStats") val userStats: List<SeriesPoint>,
    @SerializedName("courtStats") val courtStats: List<SeriesPoint>,
    @SerializedName("bookingStats") val bookingStats: List<BookingSeriesPoint>,
    @SerializedName("sportTypeStats") val sportTypeStats: List<DistPoint>,
    @SerializedName("statusStats") val statusStats: List<DistPoint>,
    @SerializedName("topCourts") val topCourts: List<TopCourtAgg>,
    @SerializedName("period") val period: Int
)

data class PlatformAnalyticsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("analytics") val analytics: PlatformAnalytics
)

data class ActivityItem(
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("timestamp") val timestamp: Date,
    @SerializedName("data") val data: JsonObject // heterogeneous: user/court/booking
)

data class RecentActivityResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("activities") val activities: List<ActivityItem>
)

data class AdminUsersPagination(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalUsers") val totalUsers: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean,
    @SerializedName("hasPrevPage") val hasPrevPage: Boolean
)

data class RoleCount(
    @SerializedName("_id") val role: String,
    @SerializedName("count") val count: Int
)

data class AdminUserManagementResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("users") val users: List<User>,
    @SerializedName("pagination") val pagination: AdminUsersPagination,
    @SerializedName("stats") val stats: List<RoleCount>
)

data class AdminUpdateUserRoleBody(
    @SerializedName("role") val role: String
)

data class AdminUpdateUserRoleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("user") val user: User,
    @SerializedName("message") val message: String
)

data class AdminDeleteUserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)




@Serializable
data class Match(
    val id: String,
    val teamA: String,
    val teamB: String,
    val matchType: String,  // ✅ add this
    val totalOvers: Int,
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val adminId: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val innings: List<CricketInnings> = emptyList(),
    val currentInnings: Int = 1,
    val currentOver: Int = 0,
    val currentBall: Int = 0,
    val currentBatsman: String? = null,
    val currentBowler: String? = null,
    val target: Int? = null,
    val requiredRuns: Int? = null,
    val requiredBalls: Int? = null,
    val runRate: Double = 0.0,
    val requiredRunRate: Double? = null
)

// Enhanced Cricket Match Models
@Serializable
data class CricketMatch(
    val id: String,
    val teamA: String,
    val teamB: String,
    val matchType: String, // "T20", "ODI", "Test"
    val totalOvers: Int,
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val adminId: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
    val innings: List<CricketInnings> = emptyList(),
    val currentInnings: Int = 1,
    val currentOver: Int = 0,
    val currentBall: Int = 0,
    val currentBatsman: String? = null,
    val currentBowler: String? = null,
    val target: Int? = null,
    val requiredRuns: Int? = null,
    val requiredBalls: Int? = null,
    val runRate: Double = 0.0,
    val requiredRunRate: Double? = null
)

@Serializable
data class CricketInnings(
    val inningsNumber: Int,
    val battingTeam: String,
    val bowlingTeam: String,
    val totalRuns: Int = 0,
    val totalWickets: Int = 0,
    val totalOvers: Double = 0.0,
    val runRate: Double = 0.0,
    val batsmen: List<Batsman> = emptyList(),
    val bowlers: List<Bowler> = emptyList(),
    val overs: List<Over> = emptyList(),
    val extras: Extras = Extras(),
    val isCompleted: Boolean = false
)

@Serializable
data class Batsman(
    val name: String,
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val strikeRate: Double = 0.0,
    val isOut: Boolean = false,
    val isOnStrike: Boolean = false
)

@Serializable
data class Bowler(
    val name: String,
    val overs: Double = 0.0,
    val maidens: Int = 0,
    val runs: Int = 0,
    val wickets: Int = 0,
    val economy: Double = 0.0,
    val isBowling: Boolean = false
)

@Serializable
data class Over(
    val overNumber: Int,
    val bowler: String,
    val balls: List<Ball> = emptyList(),
    val runs: Int = 0,
    val wickets: Int = 0,
    val extras: Int = 0
)

@Serializable
data class Ball(
    val ballNumber: Int,
    val runs: Int = 0,
    val isWide: Boolean = false,
    val isNoBall: Boolean = false,
    val isBye: Boolean = false,
    val isLegBye: Boolean = false,
    val isWicket: Boolean = false,
    val wicketType: String? = null,
    val batsman: String? = null
)

@Serializable
data class Extras(
    val wides: Int = 0,
    val noBalls: Int = 0,
    val byes: Int = 0,
    val legByes: Int = 0,
    val total: Int = 0
)

@Serializable
enum class MatchStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, ABANDONED
}

@Serializable
data class CreateMatchRequest(
    val teamA: String,
    val teamB: String
)

@Serializable
data class CreateCricketMatchRequest(
    val teamA: String,
    val teamB: String,
    val matchType: String = "T20",
    val overs: Int = 20
)

@Serializable
data class UpdateMatchStatusRequest(val status: MatchStatus)

@Serializable
data class SetAdminRequest(val adminId: String)

@Serializable
data class CricketScorePayload(
    val runs: Int,
    val wickets: Int,
    val overs: Double,
    val runRate: Double,
    val batsman: String? = null,
    val bowler: String? = null,
    val ball: Ball? = null
)

@Serializable
data class ScorePayload(
    val scoreA: Int,
    val scoreB: Int
)

// ----------------- Client Messages -----------------

@Serializable
sealed class ClientMessage {
    @Serializable
    @SerialName("score_update")
    data class ScoreUpdate(val update: CricketScorePayload) : ClientMessage()

    @Serializable
    @SerialName("cricket_score_update")
    data class CricketScoreUpdate(val update: CricketScorePayload) : ClientMessage()

    @Serializable
    @SerialName("heartbeat")
    object Heartbeat : ClientMessage()

    @Serializable
    @SerialName("request_snapshot")
    object RequestSnapshot : ClientMessage()

    @Serializable
    @SerialName("update_innings")
    data class UpdateInnings(val innings: CricketInnings) : ClientMessage()

    @Serializable
    @SerialName("update_bowler")
    data class UpdateBowler(val bowler: Bowler) : ClientMessage()

    @Serializable
    @SerialName("update_batsman")
    data class UpdateBatsman(val batsman: Batsman) : ClientMessage()
}

// ----------------- Server Messages -----------------

@Serializable
sealed class ServerMessage {
    abstract val type: String
}


@Serializable
data class ServerUpdate(
    override val type: String = "org.example.ServerUpdate",
    val match: Match
) : ServerMessage()



@Serializable
@SerialName("org.example.ServerUpdate")
data class CricketServerUpdate(
    override val type: String = "org.example.ServerUpdate",
    val match: CricketMatch
) : ServerMessage()


@Serializable
@SerialName("org.example.ServerError")
data class ServerError(
    override val type: String = "org.example.ServerError",
    val code: String,
    val message: String
) : ServerMessage()





@Serializable
@SerialName("org.example.ServerSnapshot")
data class ServerSnapshot(
    override val type: String = "org.example.ServerSnapshot",
    val match: Match
) : ServerMessage()

@Serializable
@SerialName("org.example.CricketServerSnapshot") // ✅ unique name
data class CricketServerSnapshot(
    override val type: String = "org.example.CricketServerSnapshot",
    val match: CricketMatch
) : ServerMessage()
