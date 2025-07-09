package com.example.navigationdrawerapp.api

import com.example.navigationdrawerapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header // Bu import olmalı
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather/getWeather")
    suspend fun getWeather(
        @Header("content-type") contentType: String = "application/json",

        @Query("data.lang") lang: String = "tr",
        @Query("data.city") city: String
    ): Response<WeatherResponse>
}