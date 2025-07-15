package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName


data class SilverResult(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("buyingstr")
    val buyingStr: String,
    @SerializedName("buying")
    val buying: Double,
    @SerializedName("sellingstr")
    val sellingStr: String,
    @SerializedName("selling")
    val selling: Double,
    @SerializedName("rate")
    val rate: String,
    @SerializedName("updatetime")
    val updateTime: String
)