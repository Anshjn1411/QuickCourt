package com.project.odoo_235.presentation.screens.user.screen.googlemapscreen

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation

    private val defaultLocation = LatLng(23.2156, 72.6369) // Gandhinagar fallback

    @SuppressLint("MissingPermission")
    fun fetchUserLocation() {
        viewModelScope.launch {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        _userLocation.value = LatLng(location.latitude, location.longitude)
                    } else {
                        _userLocation.value = defaultLocation
                    }
                }.addOnFailureListener {
                    _userLocation.value = defaultLocation
                }
            } catch (e: SecurityException) {
                _userLocation.value = defaultLocation
            }
        }
    }
}
