package com.example.weatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WeatherRepository()
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> get() = _weather

    private val _cityName = MutableStateFlow("Unknown")
    val cityName: StateFlow<String> get() = _cityName

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> get() = _error

    fun fetchWeather() {
        viewModelScope.launch {
            try {
                val location = getLocation()
                if (location != null) {
                    val response = repository.getWeather(
                        latitude = location.first,
                        longitude = location.second
                    )
                    _weather.value = response
                    _cityName.value = getCityName(getApplication(), location.first, location.second)
                    _error.value = ""
                } else {
                    _error.value = "Unable to retrieve location."
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to fetch weather data. Please try again."
            }
        }
    }

    private suspend fun getLocation(): Pair<Double, Double>? {
        return if (ContextCompat.checkSelfPermission(
                getApplication(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = fusedLocationClient.lastLocation.await()
            location?.let { Pair(it.latitude, it.longitude) }
        } else {
            _error.value = "Location permission not granted."
            null
        }
    }

    private suspend fun getCityName(context: Context, latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    addresses[0].locality ?: "Unknown (No City Found)"
                } else {
                    val cityName = geocoder.getFromLocationName("$latitude, $longitude", 1)
                    if (!cityName.isNullOrEmpty()) {
                        cityName[0].locality ?: "Unknown (Fallback)"
                    } else {
                        "Unknown (No Data)"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Unknown (Error Fetching City)"
            }
        }
    }
}
