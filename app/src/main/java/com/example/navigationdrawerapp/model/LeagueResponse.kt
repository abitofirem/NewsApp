package com.example.navigationdrawerapp.model // Projenizin model paketi

import com.google.gson.annotations.SerializedName

data class LeagueResponse(
    @SerializedName("success")
    val success: Boolean, // API isteğinin başarılı olup olmadığını gösterir
    @SerializedName("result")
    val result: List<League> // Lig listesini içeren alan
)