package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class MatchResult(
    @SerializedName("score")
    val score: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("away")
    val awayTeam: String?, //'away' olarak geliyor, daha açıklayıcı 'awayTeam' yaptım
    @SerializedName("home")
    val homeTeam: String? //'home' olarak geliyor, daha açıklayıcı 'homeTeam' yaptım
)