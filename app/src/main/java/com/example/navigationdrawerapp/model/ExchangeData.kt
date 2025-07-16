package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class ExchangeData(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rate")
    val rate: String, //API'den gelen verinin String olduğu görülüyor.
    @SerializedName("calculated")
    val calculated: Double
)