package com.example.weatherapp.repository

import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.network.RetrofitInstance

class WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): WeatherResponse {
        return RetrofitInstance.api.getWeather(latitude, longitude)
    }
}
