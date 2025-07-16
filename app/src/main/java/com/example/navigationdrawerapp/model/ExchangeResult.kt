package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class ExchangeResult(
    @SerializedName("base")
    val base: String,
    @SerializedName("lastupdate")
    val lastUpdate: String,
    @SerializedName("data")
    val data: List<ExchangeData>
)