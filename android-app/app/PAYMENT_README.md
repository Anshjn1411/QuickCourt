# Payment Gateway Integration

## Overview
This app includes a complete payment gateway integration with Razorpay. The system is designed to handle both demo payments (for testing) and real payments (for production).

## Features
- ✅ Complete booking flow with payment integration
- ✅ Demo payment mode for testing (no crashes)
- ✅ Real Razorpay integration for production
- ✅ Payment status tracking
- ✅ Booking confirmation after successful payment
- ✅ Error handling and crash prevention

## How to Use

### 1. Demo Payment Mode (Default)
The app is currently set to use **demo payment mode** to avoid crashes during development. In this mode:
- No real payment processing
- Simulated 2-second payment processing
- Generates demo payment IDs
- Perfect for testing the booking flow

### 2. Real Payment Mode
To enable real Razorpay payments:

1. Open `src/main/java/com/project/odoo_235/domain/paymenetgateway.kt`
2. Find the line: `private val USE_REAL_RAZORPAY = false`
3. Change it to: `private val USE_REAL_RAZORPAY = true`

### 3. Testing the Payment System

#### Option A: Test Payment Button
1. Open the app
2. Go to the home screen
3. Click "Test Payment Gateway" button
4. Click "Test Payment (₹100)" button
5. Wait for the simulated payment to complete

#### Option B: Complete Booking Flow
1. Open the app
2. Go to the home screen
3. Click "Book" on any court
4. Select date, duration, pitch size, and time slot
5. Click "Proceed"
6. Review booking summary and click "Pay"
7. Complete the payment
8. See the success screen

## File Structure

```
src/main/java/com/project/odoo_235/
├── domain/
│   └── paymenetgateway.kt          # Payment ViewModel with Razorpay integration
├── presentation/screens/bookingScreen/
│   ├── PaymentScreen.kt            # Real Razorpay payment screen
│   ├── SimplePaymentScreen.kt      # Demo payment screen
│   ├── BookingSuccessScreen.kt     # Success screen after payment
│   ├── TestPaymentScreen.kt        # Test payment screen
│   ├── BookingScreen.kt            # Booking selection screen
│   ├── bookingViewModel.kt         # Booking logic
│   └── venueDetail.kt              # Venue details and booking flow
└── presentation/navigation/
    ├── MainNavigation.kt           # Navigation with payment screens
    └── Screen.kt                   # Screen routes
```

## Configuration

### Razorpay Setup
- **Test Key**: `rzp_test_xoTUlKDCLd07l6`
- **Production Key**: Replace with your production key when going live

### AndroidManifest.xml
The manifest includes:
- Internet permissions
- Razorpay metadata
- Network security configuration

## Error Handling

The system includes comprehensive error handling:
- Try-catch blocks around all payment operations
- Graceful fallbacks for failed operations
- User-friendly error messages
- Logging for debugging

## Troubleshooting

### App Crashes on Payment
1. Make sure `USE_REAL_RAZORPAY = false` for testing
2. Check internet connection
3. Verify Razorpay key is correct
4. Check logs for specific error messages

### Payment Not Working
1. Verify Razorpay account is active
2. Check payment gateway configuration
3. Ensure proper permissions are granted
4. Test with demo payment first

## Production Deployment

When ready for production:

1. **Enable Real Payments**:
   ```kotlin
   private val USE_REAL_RAZORPAY = true
   ```

2. **Update Razorpay Key**:
   - Replace test key with production key
   - Update in both `paymenetgateway.kt` and `AndroidManifest.xml`

3. **Test Thoroughly**:
   - Test with small amounts first
   - Verify payment callbacks work
   - Check booking confirmation flow

4. **Security Considerations**:
   - Never commit production keys to version control
   - Use environment variables or secure key management
   - Implement proper server-side validation

## Support

For issues with:
- **Razorpay Integration**: Check Razorpay documentation
- **App Crashes**: Use demo mode for testing
- **Payment Flow**: Check the booking flow implementation

## Demo Mode Benefits

- ✅ No crashes during development
- ✅ Fast testing of booking flow
- ✅ No real money involved
- ✅ Easy to debug payment logic
- ✅ Perfect for demos and presentations

