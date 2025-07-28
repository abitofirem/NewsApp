package com.example.navigationdrawerapp.repository

import com.example.navigationdrawerapp.api.WeatherApiService
import com.example.navigationdrawerapp.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class WeatherRepository(private val weatherApiService: WeatherApiService) {
    
    suspend fun getWeatherForecast(city: String): Response<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            weatherApiService.getWeather(city = city)
        }
    }
} 