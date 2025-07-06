package com.example.navigationdrawerapp.model // Projenizin model paketi

import com.google.gson.annotations.SerializedName

data class League(
    @SerializedName("league")
    val leagueName: String, // API'den gelen "league" alanı için
    @SerializedName("key")
    val leagueKey: String   // API'den gelen "key" alanı için (ileride diğer API'lerde kullanacağız)
)