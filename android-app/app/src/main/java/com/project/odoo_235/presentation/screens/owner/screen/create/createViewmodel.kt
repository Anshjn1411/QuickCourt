package com.project.odoo_235.presentation.screens.owner.create

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.odoo_235.data.api.RetrofitInstance
import com.project.odoo_235.data.datastore.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CreateCourtViewModel(application: Application) : AndroidViewModel(application) {

    private val session = UserSessionManager(application)
    private val api by lazy { RetrofitInstance.getApi(session) }

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // Helper
    private fun textPart(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun imagePart(context: Context, uri: Uri, partName: String = "images"): MultipartBody.Part {
        val file = FileUtils.fromUri(context, uri)
        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, body)
    }

    fun createCourt(
        context: Context,
        // Required
        name: String,
        sportType: String,
        surfaceType: String,
        description: String,
        address: String,
        city: String,
        state: String,
        zipCode: String,
        country: String = "India",
        latitude: String,
        longitude: String,
        basePrice: String,
        currency: String = "INR",
        hourlyRate: Boolean = true,
        minPlayers: String,
        maxPlayers: String,
        length: String,
        width: String,
        unit: String = "meters",
        // Optional flags
        lightingAvailable: Boolean = false,
        lightingType: String? = null,
        lightingAdditionalCost: String? = null,
        equipmentProvided: Boolean = false,
        equipmentItemsJson: String? = null, // JSON array string: ["Racket","Shuttle"]
        equipmentRentalCost: String? = null,
        isAvailable: Boolean = true,
        maintenanceMode: Boolean = false,
        maintenanceReason: String? = null,
        maintenanceEndDate: String? = null, // ISO date
        operatingHoursJson: String? = null, // JSON object string
        amenities: List<String>? = emptyList(),
        rules: List<String>? = emptyList(),
        peakHourPrice: String? = null,
        weekendPrice: String? = null,
        images: List<Uri>? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                // Build multipart
                val parts = mutableListOf<MultipartBody.Part>()

                // Core text parts
                val mp = mutableMapOf(
                    "name" to name,
                    "sportType" to sportType,
                    "surfaceType" to surfaceType,
                    "description" to description,
                    "address" to address,
                    "city" to city,
                    "state" to state,
                    "zipCode" to zipCode,
                    "country" to country,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "basePrice" to basePrice,
                    "currency" to currency,
                    "hourlyRate" to hourlyRate.toString(),
                    "minPlayers" to minPlayers,
                    "maxPlayers" to maxPlayers,
                    "length" to length,
                    "width" to width,
                    "unit" to unit,
                    "lightingAvailable" to lightingAvailable.toString(),
                    "equipmentProvided" to equipmentProvided.toString(),
                    "isAvailable" to isAvailable.toString(),
                    "maintenanceMode" to maintenanceMode.toString()
                )

                peakHourPrice?.let { mp["peakHourPrice"] = it }
                weekendPrice?.let { mp["weekendPrice"] = it }
                lightingType?.let { mp["lightingType"] = it }
                lightingAdditionalCost?.let { mp["lightingAdditionalCost"] = it }
                equipmentItemsJson?.let { mp["equipmentItems"] = it }
                equipmentRentalCost?.let { mp["equipmentRentalCost"] = it }
                maintenanceReason?.let { mp["maintenanceReason"] = it }
                maintenanceEndDate?.let { mp["maintenanceEndDate"] = it }
                operatingHoursJson?.let { mp["operatingHours"] = it }

                // Add all flat text parts
                mp.forEach { (k, v) -> parts += MultipartBody.Part.createFormData(k, null, textPart(v)) }

                // Repeated parts
                amenities?.forEach { parts += MultipartBody.Part.createFormData("amenities", null, textPart(it)) }
                rules?.forEach { parts += MultipartBody.Part.createFormData("rules", null, textPart(it)) }

                // Images
                val imageParts = images?.map { imagePart(context, it) }

                val resp = api.createCourt(
                    name = textPart(name),
                    sportType = textPart(sportType),
                    surfaceType = textPart(surfaceType),
                    description = textPart(description),
                    address = textPart(address),
                    city = textPart(city),
                    state = textPart(state),
                    zipCode = textPart(zipCode),
                    country = textPart(country),
                    latitude = textPart(latitude),
                    longitude = textPart(longitude),
                    basePrice = textPart(basePrice),
                    peakHourPrice = peakHourPrice?.let { textPart(it) },
                    weekendPrice = weekendPrice?.let { textPart(it) },
                    currency = textPart(currency),
                    hourlyRate = textPart(hourlyRate.toString()),
                    amenities = amenities?.map { textPart(it) },
                    minPlayers = textPart(minPlayers),
                    maxPlayers = textPart(maxPlayers),
                    length = textPart(length),
                    width = textPart(width),
                    unit = textPart(unit),
                    lightingAvailable = textPart(lightingAvailable.toString()),
                    lightingType = lightingType?.let { textPart(it) },
                    lightingAdditionalCost = lightingAdditionalCost?.let { textPart(it) },
                    equipmentProvided = textPart(equipmentProvided.toString()),
                    equipmentItemsJson = equipmentItemsJson?.let { textPart(it) },
                    equipmentRentalCost = equipmentRentalCost?.let { textPart(it) },
                    isAvailable = textPart(isAvailable.toString()),
                    maintenanceMode = textPart(maintenanceMode.toString()),
                    maintenanceReason = maintenanceReason?.let { textPart(it) },
                    maintenanceEndDate = maintenanceEndDate?.let { textPart(it) },
                    operatingHoursJson = operatingHoursJson?.let { textPart(it) },
                    rules = rules?.map { textPart(it) },
                    images = imageParts
                )

                if (resp.isSuccessful && resp.body() != null) {
                    _successMessage.value = "Court created successfully"
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
}

// Minimal file helper (same as earlier)
object FileUtils {
    fun fromUri(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "court_${System.currentTimeMillis()}")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file
    }
}