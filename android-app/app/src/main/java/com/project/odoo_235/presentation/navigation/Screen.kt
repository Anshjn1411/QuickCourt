package com.project.odoo_235.presentation.navigation

sealed class Screen(val routes : String){
    object SplashScreen : Screen("splash_screen")
    object WelcomeScreen1 : Screen("welcome_screen1")
    object BasicInfoScreen : Screen("basic_info_screen")
    object ProfessionalInfoScreen : Screen("professional_info_screen")
    object EducationInfoScreen : Screen("education_info_screen")
    object FinalInfoScreen : Screen("final_info_screen")
    object Login : Screen("Log_in")
    object SignUp : Screen("sign_up")
    object createPost : Screen("create_post")
    object ForgotPassword : Screen("forgot_password")
    object MainDashBoard : Screen("main_dashboard")
    object search : Screen("search")
    object booking: Screen("booking")
    object bookingSucess: Screen("booking_success")


    object Profile: Screen("profile")

    object BookingScreen : Screen("booking/{bookingId}") {
        fun bookingScreen(bookingId: String) = "booking/$bookingId"
    }
    
    object PaymentScreen : Screen("payment") {
        fun createRoute() = "payment"
    }
    
    object BookingSuccessScreen : Screen("booking_success") {
        fun createRoute() = "booking_success"
    }
    
    object TestPaymentScreen : Screen("test_payment") {
        fun createRoute() = "test_payment"
    }
    
    object Navigation : Screen("navigation")
    object AdminNavigation : Screen("admin_navigation")
    object UserNavigation : Screen("user_navigation")
    object AdminDashboard : Screen("admin_dashboard")
    object MessageListScreen : Screen("message_list_screen")

    object ProfileViewScreen : Screen("profile_view_screen/{userId}"){
        fun createRoute(userId: String) = "profile_view_screen/$userId"
    }

    object ApplicantProfileViewScreen : Screen("ApplicantProfileViewScreen/{applicationId}") {
        fun createRoute(applicationId: String): String =
            "ApplicantProfileViewScreen/$applicationId"
    }

    // Cricket Match Screens
    object CricketMatchList : Screen("cricket_matches")
    object CricketMatchDetail : Screen("cricket_match/{matchId}") {
        fun createRoute(matchId: String) = "cricket_match/$matchId"
    }
    object CricketAdminPanel : Screen("cricket_admin/{matchId}") {
        fun createRoute(matchId: String) = "cricket_admin/$matchId"
    }

    // presentation/navigation/Screen.kt
    object MyBookings : Screen("my_bookings")
    data class BookingDetails(val id: String) : Screen("booking_details/{id}") {
        companion object { fun route(id: String) = "booking_details/$id" }
    }
    object UserBookingAnalytics : Screen("user_booking_analytics")

    // Owner-specific
    object Add : Screen("add")
    object Courts : Screen("courts")
}