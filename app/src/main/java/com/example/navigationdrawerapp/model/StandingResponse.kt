package com.example.navigationdrawerapp.model // Projenizin model paketi

import com.google.gson.annotations.SerializedName

data class StandingResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<TeamStanding>? //Takım sıralama listesi
)