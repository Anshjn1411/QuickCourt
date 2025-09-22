//package com.project.odoo_235.domain
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//
//import com.razorpay.Checkout
//import com.razorpay.PaymentResultListener
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//
//open class PaymentViewModel : ViewModel(), PaymentResultListener {
//    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.Initial)
//    val paymentStatus = _paymentStatus.asStateFlow()
//
//    val _isLoading = MutableStateFlow(false)
//    val isLoading = _isLoading.asStateFlow()
//
//    private var currentActivity: ComponentActivity? = null
//
//    // Flag to enable/disable real Razorpay integration
//    // Set to false to use demo payment and avoid crashes
//    private val USE_REAL_RAZORPAY = false
//
//    fun initiatePayment(
//        activity: ComponentActivity,
//        amount: Double,
//        description: String = "Booking Payment",
//        customerEmail: String = "user@example.com",
//        customerPhone: String = "7489869943"
//    ) {
//        if (!USE_REAL_RAZORPAY) {
//            // Use demo payment to avoid crashes
//            initiateDemoPayment(amount, description)
//            return
//        }
//
//        try {
//            _isLoading.value = true
//            currentActivity = activity
//
//            // Initialize Razorpay checkout with proper error handling
//            val checkout = Checkout()
//
//            // Set the key ID
//            checkout.setKeyID("rzp_test_xoTUlKDCLd07l6")
//
//            // Create payment options with proper validation
//            val options = JSONObject().apply {
//                put("name", "QuickCourt")
//                put("description", description)
//                put("currency", "INR")
//                put("amount", (amount * 100).toInt()) // amount in paise
//
//                // Add prefill data
//                val prefill = JSONObject().apply {
//                    put("email", customerEmail)
//                    put("contact", customerPhone)
//                }
//                put("prefill", prefill)
//
//                // Add theme
//                val theme = JSONObject().apply {
//                    put("color", "#4285F4")
//                }
//                put("theme", theme)
//
//                // Add additional options to prevent crashes
//                put("modal", JSONObject().apply {
//                    put("ondismiss", true)
//                })
//            }
//
//            // Execute on UI thread with proper error handling
//            activity.runOnUiThread {
//                try {
//                    // Preload checkout with error handling
//                    try {
//                        Checkout.preload(activity.applicationContext)
//                    } catch (e: Exception) {
//                        Log.e("PaymentViewModel", "Preload failed", e)
//                        // Continue anyway as preload is not critical
//                    }
//
//                    // Open checkout with error handling
//                    checkout.open(activity, options)
//
//                } catch (e: Exception) {
//                    Log.e("PaymentViewModel", "Error opening checkout", e)
//                    viewModelScope.launch {
//                        _paymentStatus.value = PaymentStatus.Failed(
//                            "Failed to open payment gateway: ${e.message}",
//                            code
//                        )
//                        _isLoading.value = false
//                    }
//
//                    // Show user-friendly error message
////                    try {
//                        Toast.makeText(activity, "Payment gateway is not available. Please try again later.", Toast.LENGTH_LONG).show()
//                    } catch (toastException: Exception) {
//                        Log.e("PaymentViewModel", "Error showing toast", toastException)
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("PaymentViewModel", "Payment initiation error", e)
//            viewModelScope.launch {
//                _paymentStatus.value = PaymentStatus.Failed(
//                    e.message ?: "Payment initiation failed",
//                    code
//                )
//                _isLoading.value = false
//            }
//
//            try {
//                Toast.makeText(activity, "Payment error: ${e.message}", Toast.LENGTH_SHORT).show()
//            } catch (toastException: Exception) {
//                Log.e("PaymentViewModel", "Error showing error toast", toastException)
//            }
//        }
//    }
//
//    private fun initiateDemoPayment(amount: Double, description: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            delay(2000) // Simulate processing time
//            handlePaymentSuccess("demo_payment_${System.currentTimeMillis()}")
//        }
//    }
//
//    override fun onPaymentSuccess(razorpayPaymentId: String?) {
//        Log.d("PaymentViewModel", "Payment successful. Payment ID: $razorpayPaymentId")
//        viewModelScope.launch {
//            _paymentStatus.value = PaymentStatus.Success(paymentId = razorpayPaymentId, amount = 0.0)
//            _isLoading.value = false
//        }
//        currentActivity?.let { activity ->
//            try {
//                Toast.makeText(activity, "Payment Successful!", Toast.LENGTH_LONG).show()
//            } catch (e: Exception) {
//                Log.e("PaymentViewModel", "Error showing success toast", e)
//            }
//        }
//    }
//
//    override fun onPaymentError(errorCode: Int, response: String?) {
//        Log.e("PaymentViewModel", "Payment error: $errorCode - $response")
//        viewModelScope.launch {
//            _paymentStatus.value = PaymentStatus.Failed(response ?: "Unknown payment error", code)
//            _isLoading.value = false
//        }
//        currentActivity?.let { activity ->
//            try {
//                Toast.makeText(activity, "Payment Failed: $response", Toast.LENGTH_LONG).show()
//            } catch (e: Exception) {
//                Log.e("PaymentViewModel", "Error showing error toast", e)
//            }
//        }
//    }
//
//    fun handlePaymentSuccess(paymentId: String?) {
//        viewModelScope.launch {
//            _paymentStatus.value = PaymentStatus.Success(paymentId = paymentId, amount = 0.0)
//            _isLoading.value = false
//        }
//    }
//
//    fun handlePaymentError(errorMessage: String) {
//        viewModelScope.launch {
//            _paymentStatus.value = PaymentStatus.Failed(errorMessage, code)
//            _isLoading.value = false
//        }
//    }
//
//    // Cleanup method
//    override fun onCleared() {
//        currentActivity = null
//        super.onCleared()
//    }
//
//    fun resetStatus() {
//        _paymentStatus.value = PaymentStatus.Initial
//        _isLoading.value = false
//    }
//
//    fun calculateAmount(durationHours: Int, pitchSize: String): Double {
//        val basePrice = when (pitchSize) {
//            "5 x 5" -> 500.0
//            "7 x 7" -> 800.0
//            "10 x 10" -> 1200.0
//            else -> 500.0
//        }
//        return basePrice * durationHours
//    }
//}
//
//// Sealed class to represent payment status
//sealed class PaymentStatus {
//    object Initial : PaymentStatus()
//    data class Success(val paymentId: String?, val amount: String?) : PaymentStatus()
//    data class Failed(val errorMessage: String, val code: Int) : PaymentStatus()
//}