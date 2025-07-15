package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class CriptoResult(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("changeWeek")
    val changeWeek: Double,
    @SerializedName("changeDay")
    val changeDay: Double,
    @SerializedName("changeHour")
    val changeHour: Double,
    @SerializedName("volume")
    val volume: Double,
    @SerializedName("price")
    val price: Double,
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String
)
