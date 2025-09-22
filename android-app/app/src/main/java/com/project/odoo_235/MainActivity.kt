package com.project.odoo_235

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.os.Build
import androidx.annotation.RequiresApi
import com.project.odoo_235.QuickCourt
import com.project.odoo_235.presentation.screens.user.screen.livematches.CricketMatchApp
import com.project.odoo_235.ui.theme.Odoo_235Theme
import com.razorpay.Checkout

import com.razorpay.PaymentResultListener

class MainActivity : ComponentActivity(), PaymentResultListener {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuickCourt()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources if needed
        Log.d("MainActivity", "MainActivity destroyed")
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentId", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }


}


object razorpay{
    val api= "rzp_test_xoTUlKDCLd07l6"  // Razorpay APi
}