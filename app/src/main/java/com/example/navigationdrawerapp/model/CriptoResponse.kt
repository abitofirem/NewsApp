package com.example.navigationdrawerapp.model

import com.google.gson.annotations.SerializedName

data class CriptoResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: List<CriptoResult>
)