package com.project.odoo_235.presentation.screens.user.screen.bookingScreen

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.odoo_235.razorpay

import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
open class PaymentViewModel : ViewModel(), PaymentResultListener {
    private val _paymentStatus = MutableStateFlow<PaymentStatus>(PaymentStatus.Initial)
    val paymentStatus = _paymentStatus.asStateFlow()


    private var currentActivity: ComponentActivity? = null

    open fun initiatePayment(
        activity: ComponentActivity

        ) {
        // Store current context and payment details
        currentActivity = activity

        try {
            val checkout = Checkout()

            // IMPORTANT: Replace with your actual Razorpay key
            checkout.setKeyID(razorpay.api)

            val options = JSONObject().apply {
                put("name", "Quick Court")
                put("description", "Booking")
                put("currency", "INR")

                // Convert amount to paisa (smallest currency unit)
                put("amount", (5 * 100).toString()) // Example: 500 INR

                // Optional: Prefill user details
                val prefill = JSONObject().apply {
                    put("email", "user@example.com")
                    put("contact", "7489869943")
                }
                put("prefill", prefill)

                // Optional: Theme and color
                val theme = JSONObject().apply {
                    put("color", "#4285F4")
                }
                put("theme", theme)
            }

            // Ensure Razorpay checkout is called on main thread
            activity.runOnUiThread {
                try {
                    Checkout.preload(activity.applicationContext)
                    Log.d("PaymentViewModel", "Error opening Razorpay checkout")
                    checkout.open(activity, options)
                    viewModelScope.launch {
                        delay(timeMillis = 4000)
                        handlePaymentSuccess(null)

                    }

                } catch (e: Exception) {
                    Log.e("PaymentViewModel", "Error opening Razorpay checkout", e)
                    handlePaymentError(e.message ?: "Checkout failed")
                    Toast.makeText(activity, "Payment error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("PaymentViewModel", "Payment initiation error", e)
            handlePaymentError(e.message ?: "Payment initiation failed")
            Toast.makeText(activity, "Payment error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Log.d("PaymentViewModel", "Payment successful. Payment ID: $razorpayPaymentId")
        handlePaymentSuccess(razorpayPaymentId)
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Log.e("PaymentViewModel", "Payment error: $errorCode - $response")
        handlePaymentError(response ?: "Unknown payment error")
    }

    fun calculateAmount(durationHours: Int, pitchSize: String): Double {
        val baseRatePerHour = when (pitchSize) {
            "5 x 5" -> 400.0
            "7 x 7" -> 500.0
            "10 x 10" -> 700.0
            else -> 400.0
        }
        return baseRatePerHour * durationHours
    }

    private fun handlePaymentSuccess(paymentId: String?) {
        viewModelScope.launch {
            Log.d("aaaaaaaa", "khwgdbkdbdb")
            _paymentStatus.value = PaymentStatus.Success(paymentId)
        }
    }

    private fun handlePaymentError(errorMessage: String) {
        viewModelScope.launch {
            _paymentStatus.value = PaymentStatus.Failed(errorMessage)
        }
    }

    // Cleanup method
    override fun onCleared() {
        currentActivity = null
        super.onCleared()
    }
}
// Sealed class to represent payment status
sealed class PaymentStatus {
    object Initial : PaymentStatus()
    data class Success(val paymentId: String?) : PaymentStatus()
    data class Failed(val errorMessage: String) : PaymentStatus()
}