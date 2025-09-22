package com.project.odoo_235.data.api

import com.google.gson.JsonObject
import com.project.odoo_235.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Repo {

    // ======================
    // AUTH
    // ======================

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthSuccessResponse>

    // Sign up (multipart) - send strings as RequestBody, optional avatar as Multipart
    @Multipart
    @POST("api/auth/register")
    suspend fun signupMultipart(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("confirmPassword") confirmPassword: RequestBody,
        @Part("role") role: RequestBody,
        @Part avatar: MultipartBody.Part? = null
    ): Response<AuthSuccessResponse>

    // Optional: JSON signup (if you don't send avatar)
    @POST("api/auth/register")
    suspend fun signupJson(@Body request: RegisterRequest): Response<AuthSuccessResponse>

    @POST("api/auth/password/forgot")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordSuccessResponse>

    @PUT("api/auth/password/reset/{token}")
    suspend fun resetPassword(
        @Path("token") token: String,
        @Body request: ResetPasswordRequest
    ): Response<AuthSuccessResponse>

    @GET("api/auth/logout")
    suspend fun logout(): Response<ApiMessageResponse>

    @GET("api/auth/verify-email/{token}")
    suspend fun verifyEmail(@Path("token") token: String): Response<VerifyEmailResponse>

    @POST("api/auth/resend-verification")
    suspend fun resendVerification(@Body request: ResendVerificationRequest): Response<ApiMessageResponse>

    @GET("api/auth/me")
    suspend fun currentUser(): Response<CurrentUserResponse>

    // Update profile (multipart). Note backend uses "phonephone" as field key.
    @Multipart
    @PUT("api/auth/me/update")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phonephone") phonephone: RequestBody?,
        @Part avatar: MultipartBody.Part? = null
    ): Response<CurrentUserResponse>

    @PUT("api/auth/password/update")
    suspend fun updatePassword(@Body request: UpdatePasswordRequest): Response<AuthSuccessResponse>


    // ======================
    // COURTS
    // ======================



    @GET("api/courts/{id}")
    suspend fun getCourtById(@Path("id") id: String): Response<CourtResponse>

    @GET("api/courts/search/location")
    suspend fun searchCourtsByLocation(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radiusKm: Int? = null,
        @Query("sportType") sportType: String? = null,
        @Query("maxPrice") maxPrice: String? = null
    ): Response<CourtsSearchByLocationResponse>

    // Owner: list
    @GET("api/courts/owner/courts")
    suspend fun getOwnerCourts(
        @Query("status") status: String? = null,
        @Query("approvalStatus") approvalStatus: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<OwnerCourtsListResponse>

    // Owner: statistics
    @GET("api/courts/owner/statistics")
    suspend fun getOwnerCourtStatistics(
        @Query("period") periodDays: Int? = null
    ): Response<OwnerStatisticsResponse>

    // Owner: analytics
    @GET("api/courts/owner/analytics")
    suspend fun getOwnerCourtAnalytics(
        @Query("courtId") courtId: String? = null,
        @Query("period") periodDays: Int? = null
    ): Response<OwnerAnalyticsResponse>

    // Create court (multipart). Use CreateCourtForm field names as parts; images as list of files.
    @Multipart
    @POST("api/courts")
    suspend fun createCourt(
        @Part("name") name: RequestBody,
        @Part("sportType") sportType: RequestBody,
        @Part("surfaceType") surfaceType: RequestBody,
        @Part("description") description: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city: RequestBody,
        @Part("state") state: RequestBody,
        @Part("zipCode") zipCode: RequestBody,
        @Part("country") country: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("basePrice") basePrice: RequestBody,
        @Part("peakHourPrice") peakHourPrice: RequestBody? = null,
        @Part("weekendPrice") weekendPrice: RequestBody? = null,
        @Part("currency") currency: RequestBody,
        @Part("hourlyRate") hourlyRate: RequestBody,
        @Part("amenities") amenities: List<RequestBody>? = null,
        @Part("minPlayers") minPlayers: RequestBody,
        @Part("maxPlayers") maxPlayers: RequestBody,
        @Part("length") length: RequestBody,
        @Part("width") width: RequestBody,
        @Part("unit") unit: RequestBody,
        @Part("lightingAvailable") lightingAvailable: RequestBody,
        @Part("lightingType") lightingType: RequestBody? = null,
        @Part("lightingAdditionalCost") lightingAdditionalCost: RequestBody? = null,
        @Part("equipmentProvided") equipmentProvided: RequestBody,
        @Part("equipmentItems") equipmentItemsJson: RequestBody? = null,
        @Part("equipmentRentalCost") equipmentRentalCost: RequestBody? = null,
        @Part("isAvailable") isAvailable: RequestBody,
        @Part("maintenanceMode") maintenanceMode: RequestBody,
        @Part("maintenanceReason") maintenanceReason: RequestBody? = null,
        @Part("maintenanceEndDate") maintenanceEndDate: RequestBody? = null,
        @Part("operatingHours") operatingHoursJson: RequestBody? = null,
        @Part("rules") rules: List<RequestBody>? = null,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<CourtResponse>

    // Update court (multipart, similar to create; support adding images and passing "imagesToDelete" as repeated parts if needed)
    @Multipart
    @PUT("api/courts/{id}")
    suspend fun updateCourt(
        @Path("id") id: String,
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<CourtResponse>

    @DELETE("api/courts/{id}")
    suspend fun deleteCourt(@Path("id") id: String): Response<ApiMessageResponse>

    @PUT("api/courts/{id}/availability")
    suspend fun toggleCourtAvailability(
        @Path("id") id: String,
        @Body request: ToggleAvailabilityRequest
    ): Response<CourtResponse>

    @POST("api/courts/{id}/review")
    suspend fun addCourtReview(
        @Path("id") id: String,
        @Body request: CourtReviewRequest
    ): Response<CourtResponse>

    // Admin: approval list
    @GET("api/courts/admin/approval")
    suspend fun getCourtsForApproval(
        @Query("approvalStatus") approvalStatus: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<AdminCourtsApprovalListResponse>

    // Admin: approve/reject a court
    @PUT("api/courts/admin/{id}/approve")
    suspend fun approveRejectCourt(
        @Path("id") id: String,
        @Body request: ApproveRejectCourtRequest
    ): Response<CourtResponse>


    // ======================
    // BOOKINGS
    // ======================

    @GET("api/bookings/availability")
    suspend fun getCourtAvailability(
        @Query("courtId") courtId: String,
        @Query("date") dateIso: String
    ): Response<CourtAvailabilityResponse>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<BookingSuccessResponse>

    @GET("api/bookings/user")
    suspend fun getUserBookings(
        @Query("status") status: String? = null,
        @Query("date") dateIso: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BookingsListResponse>

    @GET("api/bookings/owner")
    suspend fun getOwnerBookings(
        @Query("status") status: String? = null,
        @Query("date") dateIso: String? = null,
        @Query("courtId") courtId: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BookingsListResponse>

    @GET("api/bookings/analytics/user")
    suspend fun getUserBookingAnalytics(
        @Query("period") periodDays: Int? = null
    ): Response<JsonObject> // analytics map as per backend

    @GET("api/bookings/analytics/owner")
    suspend fun getOwnerBookingAnalytics(
        @Query("period") periodDays: Int? = null
    ): Response<JsonObject>


    @PUT("api/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") id: String,
        @Body request: UpdateBookingStatusRequest
    ): Response<BookingSuccessResponse>

    @PUT("api/bookings/{id}/payment")
    suspend fun updatePaymentStatus(
        @Path("id") id: String,
        @Body request: UpdatePaymentStatusRequest
    ): Response<BookingSuccessResponse>


    // Admin: all bookings
    @GET("api/bookings/admin/all")
    suspend fun getAllBookings(
        @Query("status") status: String? = null,
        @Query("paymentStatus") paymentStatus: String? = null,
        @Query("date") dateIso: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BookingsListResponse>


    // ======================
    // ADMIN
    // ======================

    @GET("api/admin/stats")
    suspend fun getAdminStats(@Query("period") periodDays: Int? = null): Response<AdminStatsResponse>

    @GET("api/admin/analytics")
    suspend fun getPlatformAnalytics(@Query("period") periodDays: Int? = null): Response<PlatformAnalyticsResponse>

    @GET("api/admin/activity")
    suspend fun getRecentActivity(@Query("limit") limit: Int? = null): Response<RecentActivityResponse>

    @GET("api/admin/users")
    suspend fun getAdminUsers(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("role") role: String? = null,
        @Query("search") search: String? = null
    ): Response<AdminUserManagementResponse>

    @PUT("api/admin/users/{userId}/role")
    suspend fun adminUpdateUserRole(
        @Path("userId") userId: String,
        @Body body: AdminUpdateUserRoleBody
    ): Response<AdminUpdateUserRoleResponse>

    @DELETE("api/admin/users/{userId}")
    suspend fun adminDeleteUser(@Path("userId") userId: String): Response<AdminDeleteUserResponse>

    @GET("api/courts")
    suspend fun getCourts(
        @Query("sportType") sportType: String? = null,
        @Query("city") city: String? = null,
        @Query("state") state: String? = null,
        @Query("minPrice") minPrice: String? = null,
        @Query("maxPrice") maxPrice: String? = null,
        @Query("amenitiesCsv") amenitiesCsv: String? = null,
        @Query("rating") rating: String? = null,
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10,
        @Query("sortBy") sortBy: String? = "createdAt",
        @Query("sortOrder") sortOrder: String? = "desc"
    ): Response<CourtsListResponse>

    // data/api/ApiService.kt
    @GET("api/bookings/user")
    suspend fun getUserBookings(
        @Query("page") page: Int? = 1,
        @Query("limit") limit: Int? = 10,
        @Query("sortBy") sortBy: String? = "createdAt",
        @Query("sortOrder") sortOrder: String? = "desc",
        @Query("status") status: String? = null
    ): retrofit2.Response<BookingsListResponse>

    @GET("api/bookings/{id}")
    suspend fun getBookingById(
        @Path("id") bookingId: String
    ): retrofit2.Response<BookingSuccessResponse> // { success, booking }

    @PUT("api/bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Path("id") bookingId: String,
        @Body body: CancelBookingRequest
    ): retrofit2.Response<ApiMessageResponse> // { success, message }

    @POST("api/bookings/{id}/review")
    suspend fun addBookingReview(
        @Path("id") bookingId: String,
        @Body body: BookingReviewRequest
    ): retrofit2.Response<ApiMessageResponse>

    @PUT("api/bookings/{id}/payment")
    suspend fun updateBookingPayment(
        @Path("id") bookingId: String,
        @Body body: UpdatePaymentStatusRequest
    ): retrofit2.Response<ApiMessageResponse>

    @GET("api/bookings/analytics/user")
    suspend fun getUserBookingAnalytics(): retrofit2.Response<com.google.gson.JsonObject>



}