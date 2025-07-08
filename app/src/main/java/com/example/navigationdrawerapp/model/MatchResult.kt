package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class MatchResult(
    @SerializedName("score")
    val score: String?, // Bu "0 - 1" gibi bir string içeriyor
    @SerializedName("date")
    val date: String?, // Bu "2025-05-30T17:00:00+03:00" gibi bir string içeriyor
    @SerializedName("away")
    val awayTeam: String?,
    @SerializedName("home")
    val homeTeam: String?
)