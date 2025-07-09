package com.example.navigationdrawerapp.model // Paket adınızın doğru olduğundan emin olun

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("result") //JSON'daki "result" anahtarını bu değişkene eşler
    val result: List<WeatherForecast> //Hava durumu tahminlerinin listesi
)