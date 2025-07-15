package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class GoldResult(
    @SerializedName("name")
    val name: String,
    @SerializedName("buying")
    val buying: String,
    @SerializedName("selling")
    val selling: String,
    @SerializedName("rate")
    val rate: Double? = null,
    @SerializedName("change")
    val change: String? = null
)