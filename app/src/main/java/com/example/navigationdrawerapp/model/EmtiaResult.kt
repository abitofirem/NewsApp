package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class EmtiaResult(
    @SerializedName("name")
    val name: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("selling")
    val selling: Double,
    @SerializedName("rate")
    val rate: Double
)