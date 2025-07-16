package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

//Her bir döviz kurunu temsil eden veri sınıfı
data class Currency(
    @SerializedName("name")
    val name: String,
    @SerializedName("buying")
    val buying: String,
    @SerializedName("selling")
    val selling: String,
    @SerializedName("code")
    val code: String
)